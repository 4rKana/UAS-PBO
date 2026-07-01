package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.Barang;
import com.PBO2.CampShare.entity.BarangJual;
import com.PBO2.CampShare.entity.BarangPinjam;
import com.PBO2.CampShare.service.BarangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// --- 1. TAMBAHAN IMPORT UNTUK CLOUDINARY ---
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

@RestController
@RequestMapping("/api/barang")
@CrossOrigin(origins = "*") 
public class BarangController {

    // --- 2. TAMBAHAN INJECT BEAN CLOUDINARY ---
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private BarangService barangService;

    @Autowired
    private com.PBO2.CampShare.repository.BarangRepository barangRepository;

    // --- 3. UBAH METHOD UPLOAD FOTO (MENGGUNAKAN CLOUDINARY) ---
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFoto(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File tidak boleh kosong");
        }
        
        try {
            // Mengirim file ke Cloudinary dalam bentuk byte array
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            
            // Mengambil URL aman (https://res.cloudinary.com/...) yang dihasilkan Cloudinary
            String fileUrl = uploadResult.get("secure_url").toString();
            
            // Kembalikan URL internet ini ke frontend HTML kamu
            return ResponseEntity.ok(fileUrl);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Gagal mengupload file ke Cloudinary: " + e.getMessage());
        }
    }

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
    public ResponseEntity<String> hapusBarang(
            @PathVariable Long id,
            HttpSession session) {

        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Belum login");
        }

        barangService.deleteBarang(id, userId);

        return ResponseEntity.ok("Barang berhasil dihapus");
    }

    // Endpoint untuk mengambil barang berdasarkan User ID Pemilik
    @GetMapping("/user/{idUser}")
    public ResponseEntity<?> getBarangByUser(@PathVariable String idUser) {
        try {
            List<Barang> semuaBarang = barangRepository.findByPemilikIdUser(idUser);

            List<Barang> jual = semuaBarang.stream()
                .filter(b -> b instanceof com.PBO2.CampShare.entity.BarangJual)
                .collect(java.util.stream.Collectors.toList());
                
            List<Barang> pinjam = semuaBarang.stream()
                .filter(b -> b instanceof com.PBO2.CampShare.entity.BarangPinjam)
                .collect(java.util.stream.Collectors.toList());

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("barangJual", jual);
            response.put("barangPinjam", pinjam);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}