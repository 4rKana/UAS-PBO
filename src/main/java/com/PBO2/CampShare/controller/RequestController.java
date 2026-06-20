package com.PBO2.CampShare.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;
import com.PBO2.CampShare.service.RequestService;

@RestController
@RequestMapping("/api/request")
public class RequestController {
    
    
    @Autowired
    private RequestService requestService;

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
            @RequestParam StatusRequest status) {
        return ResponseEntity.ok(requestService.ubahStatus(id, status));
    }
}   
