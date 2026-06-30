package com.PBO2.CampShare.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO2.CampShare.entity.Conversation;
import com.PBO2.CampShare.repository.ConversationRepository;
import com.PBO2.CampShare.repository.MessageRepository;

@Service
public class ChatAutoDeleter {
    private static final Logger logger = LoggerFactory.getLogger(ChatAutoDeleter.class);
    private final int retentionDays = 1;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatAutoDeleter(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    // Ini Cron Job P2P
    // @Scheduled(cron = "0 0 0 * * ?") // ini tiap jam 12 malam atau 1 harilh
    @Scheduled(fixedRate = 60000) // ini tiap 1 menit
    @Transactional
    public void runMidnightCleanup() {
        logger.info("Memulai Cron Job: Pembersihan Chat P2P Usang (> {} hari)...", retentionDays);
        
        // LocalDateTime thresholdDate = LocalDateTime.now().minusDays(retentionDays); // ini tiap 1 hari
        LocalDateTime thresholdDate = LocalDateTime.now().minusMinutes(retentionDays); // ini tiap 1 menit

        int hiddenMessagesCount = messageRepository.softDeleteOldMessages(thresholdDate);
        logger.info("Soft Delete: Berhasil menyembunyikan {} pesan tua.", hiddenMessagesCount);

        List<Conversation> expiredConversations = conversationRepository.findExpiredConversations(thresholdDate);
        
        if (expiredConversations.isEmpty()) {
            logger.info("Tidak ada ruang obrolan P2P usang yang ditemukan.");
            return;
        }

        for (Conversation conv : expiredConversations) {
            logger.info("Hard Delete: Menghapus permanen obrolan usang ID: {}", conv.getId());

            messageRepository.deleteByConversationId(conv.getId());

            conversationRepository.delete(conv);
        }
        
        logger.info("Cron Job selesai dieksekusi.");
    }

    // Gak kepake soalnya gak jadi per order, tapi p2p
    // public void executeSoftDelete(Cleanable target) {
    //     // TODO: Implementasi mengubah status isDeleted = true pada Message
    // }

    // public void executeHardDelete(Cleanable target, DeletableContext context) {
    //     if (context.isReadyToDelete()) {
    //         logger.info("Konteks mengizinkan penghapusan. Melakukan hard delete...");
    //         target.hardDelete();
    //     } else {
    //         logger.info("Transaksi terkait belum SELESAI. ChatRoom dipertahankan.");
    //     }
    // }
}
