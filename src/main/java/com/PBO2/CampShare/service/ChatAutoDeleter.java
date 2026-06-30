package com.PBO2.CampShare.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO2.CampShare.repository.ConversationRepository;
import com.PBO2.CampShare.repository.MessageRepository;

@Service
public class ChatAutoDeleter {
    private static final Logger logger = LoggerFactory.getLogger(ChatAutoDeleter.class);

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatAutoDeleter(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    // Ini Cron Job P2P
    // @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Jakarta") // ini tiap jam 12 malam atau 1 harilh
    @Scheduled(fixedRate = 60000) // ini tiap 1 menit
    @Transactional
    public void runMidnightCleanup() {
        logger.info("Memulai Cron Job: Pemeliharaan Siklus Hidup Data Chat...");
        
        // LocalDateTime hideThreshold = LocalDateTime.now().minusHours(24); // tiap 24 jam
        LocalDateTime hideThreshold = LocalDateTime.now().minusMinutes(1); // tiap 1 menit
        int hiddenCount = messageRepository.softDeleteOldMessages(hideThreshold);
        logger.info("Soft Delete: Berhasil menyembunyikan {} pesan yang lebih tua dari 24 Jam.", hiddenCount);

        // LocalDateTime destroyThreshold = LocalDateTime.now().minusDays(365); // tiap 1 Tahun
        LocalDateTime destroyThreshold = LocalDateTime.now().minusMinutes(2); // tiap 2 menit
        int destroyedCount = messageRepository.hardDeleteMessagesOlderThan(destroyThreshold);
        logger.info("Hard Delete: Berhasil memusnahkan {} pesan usang yang lebih tua dari 1 Tahun.", destroyedCount);
        
        int emptyRoomsDeleted = conversationRepository.deleteEmptyConversations();
        logger.info("Sapu Bersih : Menghapus {} ruang obrolan (Conversation) kosong.", emptyRoomsDeleted);

        logger.info("Cron Job selesai dieksekusi.");
    }
}