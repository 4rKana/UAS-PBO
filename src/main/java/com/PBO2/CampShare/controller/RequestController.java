package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;
import com.PBO2.CampShare.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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