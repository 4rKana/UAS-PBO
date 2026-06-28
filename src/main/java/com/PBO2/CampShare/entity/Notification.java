package com.PBO2.CampShare.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId; // Pemilik notifikasi (yang akan melihat notifikasi ini)

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    /**
     * Jenis notifikasi, dipakai untuk filter/ikon di frontend nantinya.
     * Disimpan sebagai String (bukan enum native DB) supaya mudah ditambah
     * jenis baru di kemudian hari tanpa migrasi skema yang kaku.
     */
    @Column(name = "type", nullable = false)
    private String type; // contoh: "REQUEST_BARANG", "TRANSAKSI_BELI", "TRANSAKSI_PINJAM"

    /**
     * ID baris terkait di tabel sumber (misal id_transaksi atau id request_barang),
     * supaya frontend bisa membuat link "lihat detail" langsung ke sana kalau perlu.
     * Boleh null kalau notifikasi tidak terkait satu baris spesifik.
     */
    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}