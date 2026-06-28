package com.PBO2.CampShare.repository;

import com.PBO2.CampShare.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Daftar notifikasi milik satu user, terbaru di atas
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    // Hitung jumlah notifikasi yang belum dibaca (untuk titik merah)
    long countByUserIdAndIsReadFalse(String userId);

    // Tandai semua notifikasi milik satu user sebagai sudah dibaca
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") String userId);
}