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
    public ResponseEntity<?> create(@RequestBody RequestBarang request) {
        
        // Cukup panggil satu fungsi saja agar tidak double save
        try {
            RequestBarang savedRequest = requestService.buatRequest(request);
            return ResponseEntity.ok(savedRequest);
        } catch (IllegalArgumentException e) {
        // Ini akan mengirim pesan error ke frontend
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RequestBarang> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            StatusRequest statusEnum = StatusRequest.valueOf(status.toUpperCase());
            return ResponseEntity.ok(requestService.ubahStatus(id, statusEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
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
}