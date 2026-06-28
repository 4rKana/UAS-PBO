package com.PBO2.CampShare.service;



import java.util.List;

import org.springframework.stereotype.Service;

import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;
import com.PBO2.CampShare.repository.RequestRepository;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    // CONSTRUCTOR INJECTION (Memenuhi DIP - Dependency Inversion Principle)
    // Sangat direkomendasikan untuk kemudahan Unit Testing & Immutability
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<RequestBarang> getAllRequest() {
        return requestRepository.findAll();
    }

    // --- TAMBAHKAN FUNGSI INI DI SINI ---
    // Fungsi ini untuk memfilter data berdasarkan status
    public List<RequestBarang> getRequestByStatus(StatusRequest status) {
        return requestRepository.findByStatusRequest(status);
    }

    public RequestBarang buatRequest(RequestBarang request) {

        // Pastikan request tidak null
        if (request == null) {
            throw new IllegalArgumentException("Data request tidak boleh kosong");
        }
        // Aturan Bisnis: Setiap request baru wajib berstatus TERSEDIA
        request.setStatusRequest(StatusRequest.TERSEDIA);

        // 2. Tambahkan validasi userId (Opsional tapi sangat disarankan)
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID tidak valid atau tidak ditemukan!");
        }
        
        return requestRepository.save(request);
    }
    // DISATUKAN: Cukup satu fungsi update status yang menerima tipe data Enum yang aman (Type-Safe)
    public RequestBarang ubahStatus(Long id, StatusRequest statusBaru, String userIdLogin) {
        RequestBarang request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));
        
        if(request.getUserId().equals(userIdLogin)) {
            throw new RuntimeException("Anda tidak bisa menawar barang anda sendiri");
        }

        // 2. Hanya status TERSEDIA yang bisa diubah jadi TERPENUHI (untuk tawar barang)
        if (request.getStatusRequest() != StatusRequest.TERSEDIA) {
            throw new RuntimeException("Barang sudah tidak tersedia untuk ditawar!");
        }
        
        request.setStatusRequest(statusBaru);
        return requestRepository.save(request);
    }

    public void hapusRequest(Long id, String userIdLogin) {
        RequestBarang requestBarang = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));

        if (!requestBarang.getUserId().equals(userIdLogin)) {
            throw new RuntimeException("Anda tidak dapat memiliki izin untuk menghapus data ini!");
        }

        requestRepository.delete(requestBarang);
    }
} 