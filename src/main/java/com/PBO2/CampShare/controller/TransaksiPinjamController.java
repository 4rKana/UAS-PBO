package com.PBO2.CampShare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.PBO2.CampShare.entity.TransaksiPinjam;
import com.PBO2.CampShare.service.TransaksiPinjamService;

@RestController
@RequestMapping("/api/pinjam")
@CrossOrigin(origins = "*")
public class TransaksiPinjamController {

    private final TransaksiPinjamService service;

    public TransaksiPinjamController(
            TransaksiPinjamService service) {
        this.service = service;
    }

    @PostMapping
    public TransaksiPinjam create(
            @RequestBody TransaksiPinjam transaksi) {

        return service.create(transaksi);
    }

    @GetMapping
    public List<TransaksiPinjam> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TransaksiPinjam getById(
            @PathVariable String id) {

        return service.getById(id);
    }

    @PutMapping("/{id}")
    public TransaksiPinjam update(
            @PathVariable String id,
            @RequestBody TransaksiPinjam transaksi) {

        return service.updateStatus(id, transaksi);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable String id) {

        service.delete(id);
    }
    
    @Autowired
    private com.PBO2.CampShare.repository.TransaksiPinjamRepository pinjamRepository;

    // 2. Tambahkan Endpoint khusus untuk mengambil riwayat berdasarkan user
    @GetMapping("/user/{idUser}")
    public List<TransaksiPinjam> getRiwayatUser(@PathVariable String idUser) {
        return pinjamRepository.findByPeminjamIdUser(idUser);
    }
}