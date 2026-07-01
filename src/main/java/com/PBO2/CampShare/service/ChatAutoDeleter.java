package com.PBO2.CampShare.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.PBO2.CampShare.entity.Conversation;
import com.PBO2.CampShare.entity.TransaksiBeli;
import com.PBO2.CampShare.entity.TransaksiPinjam;
import com.PBO2.CampShare.entity.enumeration.StatusTransaksiBeli;
import com.PBO2.CampShare.entity.enumeration.StatusTransaksiPinjam;
import com.PBO2.CampShare.repository.ConversationRepository;
import com.PBO2.CampShare.repository.MessageRepository;
import com.PBO2.CampShare.repository.TransaksiBeliRepository;
import com.PBO2.CampShare.repository.TransaksiPinjamRepository;

@Service
public class ChatAutoDeleter {
    private static final Logger logger = LoggerFactory.getLogger(ChatAutoDeleter.class);

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final TransaksiPinjamRepository pinjamRepo;
    private final TransaksiBeliRepository beliRepo;

    public ChatAutoDeleter(ConversationRepository conversationRepository, MessageRepository messageRepository, 
                           TransaksiPinjamRepository pinjamRepo, TransaksiBeliRepository beliRepo) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.pinjamRepo = pinjamRepo;
        this.beliRepo = beliRepo;
    }

    @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Jakarta") // setiap tanggal 1
    // @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Jakarta") // buat tiap malem jam 00:00
    // @Scheduled(fixedRate = 30000) // buat 30 detik
    
    @Transactional
    public void runCleanupCycle() {
        logger.info("Memulai Cron Job: Mengecek obrolan yang sudah tidak aktif dan siap dihapus.");
        
        List<Conversation> semuaObrolan = conversationRepository.findAll();
        
        for (Conversation conv : semuaObrolan) {
            String u1 = conv.getUser1Id();
            String u2 = conv.getUser2Id();
            
            List<TransaksiPinjam> txPinjam = pinjamRepo.findTransaksiAntaraDuaUser(u1, u2);
            List<TransaksiBeli> txBeli = beliRepo.findTransaksiAntaraDuaUser(u1, u2);
            
            boolean adaTransaksi = !txPinjam.isEmpty() || !txBeli.isEmpty();
            
            if (!adaTransaksi) {
                // LocalDateTime batasTanpaTx = conv.getCreatedAt().plusDays(3); // buat 3 hari
                LocalDateTime batasTanpaTx = conv.getCreatedAt().plusMinutes(2); // buat 2 menit
                
                if (LocalDateTime.now().isAfter(batasTanpaTx)) {
                    int hidden = messageRepository.softDeleteByConversationId(conv.getId());
                    if (hidden > 0) logger.info("Soft Delete: Obrolan ID {} (Masa negosiasi usang).", conv.getId());
                }
            } else {
                boolean adaYangBelumSelesai = cekTransaksiGantung(txPinjam, txBeli);
                
                if (adaYangBelumSelesai) {
                    continue;
                }

                LocalDateTime waktuSelesaiTerakhir = cariWaktuSelesaiTerbaru(txPinjam, txBeli, conv.getCreatedAt());

                // LocalDateTime batasSelesai = waktuSelesaiTerakhir.plusDays(1); // buat 1 hari
                LocalDateTime batasSelesai = waktuSelesaiTerakhir.plusMinutes(1); // buat 1 menit
                
                if (LocalDateTime.now().isAfter(batasSelesai)) {
                    int hidden = messageRepository.softDeleteByConversationId(conv.getId());
                    if (hidden > 0) logger.info("Soft Delete: Obrolan ID {} (Masa post-sales usang).", conv.getId());
                }
            }
        }

        eksekusiHardDelete();
    }

    private boolean cekTransaksiGantung(List<TransaksiPinjam> pinjam, List<TransaksiBeli> beli) {
        for (TransaksiPinjam p : pinjam) {
            if (p.getStatus() != StatusTransaksiPinjam.SELESAI) return true;
        }
        for (TransaksiBeli b : beli) {
            if (b.getStatus() != StatusTransaksiBeli.SELESAI) return true;
        }
        return false;
    }

    private LocalDateTime cariWaktuSelesaiTerbaru(List<TransaksiPinjam> pinjam, List<TransaksiBeli> beli, LocalDateTime defaultTime) {
        LocalDateTime terbaru = defaultTime;
        
        for (TransaksiPinjam p : pinjam) {
            if (p.getTanggalSelesai() != null) {
                LocalDateTime waktuPinjam = p.getTanggalSelesai().atTime(23, 59, 59);
                if (waktuPinjam.isAfter(terbaru)) terbaru = waktuPinjam;
            }
        }
        
        for (TransaksiBeli b : beli) {
            if (b.getWaktuSelesai() != null && b.getWaktuSelesai().isAfter(terbaru)) {
                terbaru = b.getWaktuSelesai();
            }
        }
        return terbaru;
    }

    private void eksekusiHardDelete() {
        // LocalDateTime destroyThreshold = LocalDateTime.now().minusDays(365); // buat 1 tahun
        LocalDateTime destroyThreshold = LocalDateTime.now().minusMinutes(3); // buat 3 menit
        
        int destroyedCount = messageRepository.hardDeleteMessagesOlderThan(destroyThreshold);
        if (destroyedCount > 0) logger.info("Hard Delete (m): Memusnahkan {} pesan permanen.", destroyedCount);
        
        int emptyRoomsDeleted = conversationRepository.deleteEmptyConversations();
        if (emptyRoomsDeleted > 0) logger.info("Hard Delete (c): Menghapus {} ruang obrolan (Conversation) kosong.", emptyRoomsDeleted);
    }
}