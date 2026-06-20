package com.PBO2.CampShare.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;

import org.springframework.beans.factory.annotation.Autowired;
import com.PBO2.CampShare.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/request")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class RequestController {
    
    private final RequestService requestService;

    // CONSTRUCTOR INJECTION (Memenuhi SOLID - Dependency Inversion Principle)
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<RequestBarang>> ambilSemuaRequest(){
        return ResponseEntity.ok(requestService.getAllRequest());
    }

    @PostMapping
    public ResponseEntity<RequestBarang> tambahRequest(@RequestBody RequestBarang request) {
        return ResponseEntity.ok(requestService.buatRequest(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RequestBarang> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {

        // 1. Ambil data string dari JSON frontend
        String statusBaruString = requestBody.get("statusRequest");
        
        // 2. Konversi dari String ke Enum di layer Controller (Memenuhi Single Responsibility)
        StatusRequest statusBaruEnum = StatusRequest.valueOf(statusBaruString);
        
        // 3. Panggil method Service yang baru dan type-safe
        RequestBarang updateData = requestService.ubahStatus(id, statusBaruEnum);
        return ResponseEntity.ok(updateData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteRequest(@PathVariable Long id) {
        // Hapus dari database melalui service
        requestService.hapusRequest(id);
        
        // Berikan response kalau data sukses terhapus
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        
        return ResponseEntity.ok(response);
    }   
}