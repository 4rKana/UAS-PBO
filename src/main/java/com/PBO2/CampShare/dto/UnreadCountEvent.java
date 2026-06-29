package com.PBO2.CampShare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload yang di-broadcast lewat WebSocket ke topic /topic/unread/{userId},
 * setiap kali jumlah pesan belum dibaca (global, lintas semua conversation)
 * milik seorang user berubah — baik karena ada pesan baru masuk, maupun
 * karena user menandai sebuah percakapan sebagai sudah dibaca.
 *
 * Dipakai untuk meng-update titik merah indikator chat secara real-time
 * di halaman LAIN (misal notifikasi.html), tanpa perlu reload halaman
 * tersebut atau polling berkala.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCountEvent {
    private long unreadCount;
}