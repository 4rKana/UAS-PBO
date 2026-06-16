package com.PBO2.CampShare.controller;


import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;

import org.springframework.beans.factory.annotation.Autowired;
import com.PBO2.CampShare.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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
