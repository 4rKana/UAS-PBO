package com.PBO2.CampShare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;
import com.PBO2.CampShare.service.RequestService;

@RestController
@RequestMapping("/api/request")
@CrossOrigin(origins = "*")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<RequestBarang>> getAll() {
        return ResponseEntity.ok(requestService.getAllRequest());
    }

    // --- TAMBAHAN: Endpoint untuk Filter Status ---
    @GetMapping("/filter")
    public ResponseEntity<List<RequestBarang>> getByStatus(@RequestParam String status) {
        try {
            StatusRequest statusEnum = StatusRequest.valueOf(status.toUpperCase());
            return ResponseEntity.ok(requestService.getRequestByStatus(statusEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
        public ResponseEntity<RequestBarang> create(@RequestBody RequestBarang request, @RequestHeader("userId") String userId) {
            request.setUserId(userId);
            request.setStatusRequest(StatusRequest.TERSEDIA);
            // Opsional: Pastikan field ini kosong saat baru buat
            request.setAcceptedByUserId(null);
            return ResponseEntity.ok(requestService.buatRequest(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, 
                                                        @RequestParam String status,
                                                        @RequestHeader("userId") String userIdLogin) {
         try {
            StatusRequest statusEnum = StatusRequest.valueOf(status.toString().toUpperCase()); 
            RequestBarang updateRequest = requestService.ubahStatus(id, statusEnum, userIdLogin);
            return ResponseEntity.ok(updateRequest);
        } catch (IllegalArgumentException e) {
            // Menangani jika status string tidak cocok dengan Enum
            return ResponseEntity.badRequest().body("Status tidak valid!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader("userId") String userId) {
        try {
            requestService.hapusRequest(id, userId);
            return ResponseEntity.ok("Data berhasil dihapus");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }

    }

    @PutMapping("/{id}/terima")
    public ResponseEntity<?> terimaRequest(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        try {
            String penolongId = body.get("penolongId");
            requestService.terimaRequest(id, penolongId);
            return ResponseEntity.ok("Request berhasil diterima!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/selesai")
    public ResponseEntity<?> selesaikanRequest(@PathVariable Long id, @RequestHeader("userId") String userIdLogin) {
        try {
            requestService.selesaikanRequest(id, userIdLogin);
            return ResponseEntity.ok("Transaksi selesai!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}