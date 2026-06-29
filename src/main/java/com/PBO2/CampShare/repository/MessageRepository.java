package com.PBO2.CampShare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.PBO2.CampShare.entity.Message;

public interface MessageRepository
        extends JpaRepository<Message, Integer> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Integer conversationId);
            // Fungsi untuk Soft Delete (menyembunyikan pesan tua)
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Message m SET m.isDeleted = true WHERE m.createdAt < :threshold")
    int softDeleteOldMessages(@org.springframework.data.repository.query.Param("threshold") java.time.LocalDateTime threshold);

    // Fungsi untuk Hard Delete (menghapus pesan secara permanen berdasarkan ID Obrolan)
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Message m WHERE m.conversationId = :conversationId")
    void deleteByConversationId(@org.springframework.data.repository.query.Param("conversationId") Integer conversationId);

    /**
     * Menghitung jumlah pesan BELUM DIBACA milik seorang user, dihitung
     * lintas SEMUA conversation yang dia ikuti.
     *
     * Logikanya:
     * - Ambil semua conversation di mana user adalah user1_id ATAU user2_id
     * - Untuk tiap conversation itu, hitung pesan yang BUKAN dari dirinya sendiri
     *   (sender_id != :userId) dan dikirim SETELAH last_read_at miliknya
     * - Kalau belum pernah ada baris last_read_at sama sekali untuk pasangan
     *   (conversation, user) tersebut (LEFT JOIN menghasilkan NULL), maka
     *   SEMUA pesan masuk di conversation itu dihitung sebagai belum dibaca
     *   (anggap created_at > '1970-01-01' selalu benar).
     */
    @Query(value = """
            SELECT COUNT(*) FROM messages m
            JOIN conversations c ON m.conversation_id = c.id
            LEFT JOIN conversation_read_status crs
                ON crs.conversation_id = c.id AND crs.user_id = :userId
            WHERE (c.user1_id = :userId OR c.user2_id = :userId)
              AND m.sender_id != :userId
              AND m.created_at > COALESCE(crs.last_read_at, '1970-01-01 00:00:00')
            """, nativeQuery = true)
    long countUnreadMessages(@Param("userId") String userId);

    /**
     * Sama seperti countUnreadMessages, tapi dibatasi untuk SATU conversation
     * spesifik saja. Dipakai untuk menampilkan titik merah pada avatar
     * masing-masing item di daftar chat (chat list), bukan titik merah
     * global pada tombol chat.
     */
    @Query(value = """
            SELECT COUNT(*) FROM messages m
            LEFT JOIN conversation_read_status crs
                ON crs.conversation_id = m.conversation_id AND crs.user_id = :userId
            WHERE m.conversation_id = :conversationId
              AND m.sender_id != :userId
              AND m.created_at > COALESCE(crs.last_read_at, '1970-01-01 00:00:00')
            """, nativeQuery = true)
    long countUnreadMessagesInConversation(@Param("conversationId") Integer conversationId, @Param("userId") String userId);
}