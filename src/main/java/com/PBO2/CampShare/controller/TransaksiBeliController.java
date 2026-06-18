package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.TransaksiBeli;
import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.service.TransaksiBeliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaksi-beli")
@CrossOrigin(origins = "*") // Wajib untuk Frontend (HTML/JS lokal)
public class TransaksiBeliController {

    @Autowired
    private TransaksiBeliService service;

    // 1. Endpoint untuk membuat transaksi baru (Murni tambahan baru karena di Athar belum ada)
    @PostMapping("/buat")
    public TransaksiBeli buatTransaksiBaru(@RequestBody TransaksiBeli requestTransaksi) {
        return service.buatTransaksiBaru(requestTransaksi.getPembeli(), requestTransaksi.getBarang());
    }

    // 2. Mengambil semua transaksi (Bawaan Athar)
    @GetMapping
    public List<TransaksiBeli> getAll() {
        return service.getAllTransaksi();
    }

    // 3. Memproses booking / menyetujui (Bawaan Athar)
    @PostMapping("/{id}/booking")
    public TransaksiBeli booking(@PathVariable String id) {
        return service.prosesBooking(id);
    }

    // 4. Mengkonfirmasi barang sudah diterima / selesai (Bawaan Athar)
    @PostMapping("/{id}/selesai")
    public TransaksiBeli selesai(@PathVariable String id) {
        return service.konfirmasiSelesai(id);
    }

    // 5. Endpoint untuk melihat riwayat pembelian user tertentu (Tambahan untuk profil user)
    @GetMapping("/user/{idUser}")
    public List<TransaksiBeli> getRiwayatUser(@PathVariable String idUser) {
        User dummyUser = new User();
        dummyUser.setIdUser(idUser);
        return service.getRiwayatPembelian(dummyUser);
    }
}