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

        // Aturan Bisnis: Setiap request baru wajib berstatus TERSEDIA
        request.setStatusRequest(StatusRequest.TERSEDIA);
        return requestRepository.save(request);
    }
    // DISATUKAN: Cukup satu fungsi update status yang menerima tipe data Enum yang aman (Type-Safe)
    public RequestBarang ubahStatus(Long id, StatusRequest statusBaru, String userIdLogin) {
        RequestBarang request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));
        request.setStatusRequest(statusBaru);

        return requestRepository.save(request);
    }

    public void terimaRequest(Long id, String penolongId) {
        RequestBarang request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));

        if(request.getUserId().equals(penolongId)) {
            throw new RuntimeException("Anda tidak bisa menerima request milik anda sendiri!");
        }
        if(request.getStatusRequest() != StatusRequest.TERSEDIA) {
            throw new RuntimeException("Barang tidak tersedia!");
        }

        request.setStatusRequest(StatusRequest.TERPENUHI);
        request.setAcceptedByUserId(penolongId);
        requestRepository.save(request);
    }
    // 2. Fungsi untuk pemilik request menyelesaikan transaksi
    public void selesaikanRequest(Long id, String userIdLogin) {
        RequestBarang request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));
        
        // Validasi: Hanya pemilik request yang bisa menyelesaikan
        if (!request.getUserId().equals(userIdLogin)) {
            throw new RuntimeException("Hanya pemilik request yang bisa menyelesaikan transaksi!");
        }

        request.setStatusRequest(StatusRequest.SELESAI);
        requestRepository.save(request);
    }

    public void hapusRequest(Long id, String userIdLogin) {
        RequestBarang requestBarang = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));
        
        // TAMBAHKAN VALIDASI INI:
        if (!requestBarang.getUserId().equals(userIdLogin)) {
            throw new RuntimeException("Anda tidak berhak menghapus request milik orang lain!");
        }
        
        requestRepository.delete(requestBarang);
    }
} 