package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.TransaksiBeli;
import com.PBO2.CampShare.service.TransaksiBeliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaksi-beli")
public class TransaksiBeliController {

    @Autowired
    private TransaksiBeliService service;

    @GetMapping
    public List<TransaksiBeli> getAll() {
        return service.getAllTransaksi();
    }

    @PostMapping("/{id}/booking")
    public TransaksiBeli booking(@PathVariable String id) {
        return service.prosesBooking(id);
    }

    @PostMapping("/{id}/selesai")
    public TransaksiBeli selesai(@PathVariable String id) {
        return service.konfirmasiSelesai(id);
    }
}