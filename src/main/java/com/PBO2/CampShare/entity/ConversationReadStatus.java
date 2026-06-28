package com.PBO2.CampShare.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Melacak kapan terakhir seorang user "membuka"/membaca sebuah conversation.
 * Dipakai HANYA untuk menghitung indikator titik merah pada tombol chat
 * (apakah ada pesan baru yang belum dibuka), TIDAK terkait dengan sistem
 * notifikasi generik (request/transaksi) yang punya tabel terpisah.
 *
 * Satu baris = satu pasangan (conversationId, userId).
 */
@Entity
@Table(name = "conversation_read_status",
       uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"}))
@Getter
@Setter
public class ConversationReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Integer conversationId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;
}