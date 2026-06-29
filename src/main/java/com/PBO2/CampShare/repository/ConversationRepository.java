package com.PBO2.CampShare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.PBO2.CampShare.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    
    List<Conversation> findByUser1IdOrUser2Id(String user1Id, String user2Id);

    // 1. Cari tahu apakah obrolan antara kedua user ini sudah pernah ada sebelumnya
    @Query("SELECT c FROM Conversation c WHERE (c.user1Id = :u1 AND c.user2Id = :u2) OR (c.user1Id = :u2 AND c.user2Id = :u1)")
    Optional<Conversation> findConversationBetween(@Param("u1") String u1, @Param("u2") String u2);

    // 2. Validasi apakah ID User target terdaftar di tabel users database
    @Query(value = "SELECT COUNT(*) FROM users WHERE id_user = :userId", nativeQuery = true)
    int checkUserExists(@Param("userId") String userId);

    // 3. Ambil nama/username dari tabel users (Sesuaikan nama kolom 'username' jika di DB-mu berbeda)
    @Query(value = "SELECT username FROM users WHERE id_user = :userId", nativeQuery = true)
    String findNamaByUserId(@Param("userId") String userId);

    // 4. Cari ID asli (id_user) berdasarkan USERNAME yang diketik user di kotak pencarian.
    //    Dipakai saat memulai obrolan baru, karena yang diketik user adalah username,
    //    bukan id_user secara langsung.
    @Query(value = "SELECT id_user FROM users WHERE username = :username", nativeQuery = true)
    String findUserIdByUsername(@Param("username") String username);

    // Fungsi untuk mencari semua obrolan yang usianya melebihi batas waktu retensi
    @Query("SELECT c FROM Conversation c WHERE c.createdAt < :expiryDate")
    List<Conversation> findExpiredConversations(@Param("expiryDate") java.time.LocalDateTime expiryDate);
}