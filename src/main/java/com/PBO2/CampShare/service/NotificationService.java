package com.PBO2.CampShare.service;

import com.PBO2.CampShare.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    /**
     * Membuat satu notifikasi baru untuk seorang user.
     * Method ini yang akan dipanggil dari modul lain (request_barang,
     * transaksi_beli, transaksi_pinjam) setiap kali ada event yang relevan,
     * misal status transaksi berubah dari DIAJUKAN -> DISETUJUI.
     *
     * @param userId       ID user yang akan menerima notifikasi ini
     * @param title        Judul singkat, misal "Transaksi Disetujui"
     * @param message      Isi pesan notifikasi yang lebih detail
     * @param type         Jenis notifikasi, misal "TRANSAKSI_BELI"
     * @param referenceId  ID baris terkait di tabel sumber (boleh null)
     */
    void createNotification(String userId, String title, String message, String type, String referenceId);

    List<NotificationDTO> getNotifications(String userId);

    long getUnreadCount(String userId);

    void markAsRead(Long notificationId);

    void markAllAsRead(String userId);
}