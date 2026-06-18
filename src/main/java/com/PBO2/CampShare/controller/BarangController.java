package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.Barang;
import com.PBO2.CampShare.entity.BarangJual;
import com.PBO2.CampShare.entity.BarangPinjam;
import com.PBO2.CampShare.service.BarangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barang")
@CrossOrigin(origins = "*") // Penting: Agar tidak error CORS saat dihubungkan ke HTML lokal
public class BarangController {

    @Autowired
    private BarangService barangService;

    // Endpoint untuk memajang barang JUAL
    @PostMapping("/jual")
    public ResponseEntity<Barang> tambahBarangJual(@RequestBody BarangJual barangJual) {
        Barang saved = barangService.saveBarang(barangJual);
        return ResponseEntity.ok(saved);
    }

    // Endpoint untuk memajang barang PINJAM
    @PostMapping("/pinjam")
    public ResponseEntity<Barang> tambahBarangPinjam(@RequestBody BarangPinjam barangPinjam) {
        Barang saved = barangService.saveBarang(barangPinjam);
        return ResponseEntity.ok(saved);
    }

    // Endpoint mengambil seluruh katalog barang
    @GetMapping
    public ResponseEntity<List<Barang>> ambilSemuaBarang() {
        return ResponseEntity.ok(barangService.getAllBarang());
    }

    // Endpoint memfilter katalog barang jual saja
    @GetMapping("/jual")
    public ResponseEntity<List<Barang>> ambilSemuaBarangJual() {
        return ResponseEntity.ok(barangService.getAllBarangJual());
    }

    // Endpoint memfilter katalog barang pinjam saja
    @GetMapping("/pinjam")
    public ResponseEntity<List<Barang>> ambilSemuaBarangPinjam() {
        return ResponseEntity.ok(barangService.getAllBarangPinjam());
    }

    // Endpoint melihat detail satu barang
    @GetMapping("/{id}")
    public ResponseEntity<Barang> ambilBarangBerdasarkanId(@PathVariable Long id) {
        return barangService.getBarangById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint untuk menghapus barang
    @DeleteMapping("/{id}")
    public ResponseEntity<String> hapusBarang(@PathVariable Long id) {
        barangService.deleteBarang(id);
        return ResponseEntity.ok("Barang dengan ID " + id + " berhasil dihapus.");
    }
}