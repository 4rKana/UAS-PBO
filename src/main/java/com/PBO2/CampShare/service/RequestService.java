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
        // Aturan Bisnis: Setiap request baru wajib berstatus MENCARI
        request.setStatusRequest(StatusRequest.MENCARI);
        return requestRepository.save(request);
    }
    // DISATUKAN: Cukup satu fungsi update status yang menerima tipe data Enum yang aman (Type-Safe)
    public RequestBarang ubahStatus(Long id, StatusRequest statusBaru) {
        RequestBarang request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));
        request.setStatusRequest(statusBaru);

        return requestRepository.save(request);
    }

    public void hapusRequest(Long id) {
        RequestBarang requestBarang = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request dengan ID " + id + " tidak ditemukan"));
        requestRepository.delete(requestBarang);
    }
} 