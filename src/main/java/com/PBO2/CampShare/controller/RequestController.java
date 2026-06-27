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
    public ResponseEntity<RequestBarang> create(@RequestBody RequestBarang request) {
        
        // Cukup panggil satu fungsi saja agar tidak double save
        RequestBarang savedRequest = requestService.buatRequest(request);
    
        return ResponseEntity.ok(requestService.buatRequest(request));
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        requestService.hapusRequest(id);
        return ResponseEntity.noContent().build();
    }
}