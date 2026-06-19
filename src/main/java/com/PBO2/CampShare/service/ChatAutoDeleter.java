package com.PBO2.CampShare.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatAutoDeleter {
    private static final Logger logger = LoggerFactory.getLogger(ChatAutoDeleter.class);
    private final int retentionDays = 1;

    // @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedRate = 60000)
    public void runMidnightCleanup() {
        logger.info("Mulai mengeksekusi Cron Job: Pembersihan ChatRoom...");
        // ntar logikanya kalo dh beres ChatRoomRepository, ChatRoom sama Transaksi
        logger.info("Cron Job selesai dieksekusi.");
    }

    public void executeSoftDelete(Cleanable target) {
        // TODO: Implementasi mengubah status isDeleted = true pada Message
    }

    public void executeHardDelete(Cleanable target, DeletableContext context) {
        if (context.isReadyToDelete()) {
            logger.info("Konteks mengizinkan penghapusan. Melakukan hard delete...");
            target.hardDelete();
        } else {
            logger.info("Transaksi terkait belum SELESAI. ChatRoom dipertahankan.");
        }
    }
}
