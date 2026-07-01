package com.PBO2.CampShare.service;



import java.util.List;

import org.springframework.stereotype.Service;

import com.PBO2.CampShare.dto.RequestResponse;
import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;
import com.PBO2.CampShare.repository.RequestRepository;
import com.PBO2.CampShare.repository.UserRepository;
import com.PBO2.CampShare.entity.User;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    // CONSTRUCTOR INJECTION (Memenuhi DIP - Dependency Inversion Principle)
    // Sangat direkomendasikan untuk kemudahan Unit Testing & Immutability
    public RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public List<RequestResponse> getAllRequest() {
        return requestRepository.findAll().stream().map(request -> {

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            RequestResponse dto = new RequestResponse();

            dto.setId(request.getId());
            dto.setNamaBarang(request.getNamaBarang());
            dto.setDeskripsi(request.getDeskripsi());
            dto.setAnggaran(request.getAnggaran());
            dto.setUserId(request.getUserId());
            dto.setAcceptedByUserId(request.getAcceptedByUserId());
            dto.setStatusRequest(request.getStatusRequest());
            dto.setUsername(user.getUsername());

            return dto;

        }).toList();
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

    public void batalkanPilihan(Long id, String userIdLogin) {
        RequestBarang request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));
            
        if(!request.getUserId().equals(userIdLogin)) {
            throw new RuntimeException("Hanya pemilik request yang dapat membatalkan pilihan ini");
        }

        if(request.getStatusRequest() != StatusRequest.TERPENUHI) {
            throw new RuntimeException("Request tidak sedang dalam status TERPENUHI, tidak dapat dibatalkan");
        }

        request.setStatusRequest(StatusRequest.TERSEDIA);
        request.setAcceptedByUserId(null);
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

    public String getUsernameByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        return user.getUsername();
    }
} 