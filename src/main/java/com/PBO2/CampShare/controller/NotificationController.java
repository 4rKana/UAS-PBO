package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.dto.NotificationDTO;
import com.PBO2.CampShare.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Daftar semua notifikasi milik user (untuk halaman notifikasi.html)
    @GetMapping("/{userId}")
    public List<NotificationDTO> getNotifications(@PathVariable String userId) {
        return notificationService.getNotifications(userId);
    }

    // Jumlah notifikasi belum dibaca (untuk titik merah di tombol bel)
    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // Tandai satu notifikasi sebagai sudah dibaca (misal saat user klik notifikasi itu)
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // Tandai SEMUA notifikasi milik user sebagai sudah dibaca (misal saat halaman notifikasi dibuka)
    @PatchMapping("/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}