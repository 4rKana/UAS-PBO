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
    // Endpoint khusus untuk menerima upload file gambar
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFoto(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            // 1. Tentukan folder penyimpanan (Kita simpan di dalam folder static/uploads)
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // Buat foldernya jika belum ada
            }

            // 2. Buat nama file unik agar tidak bentrok (menggunakan waktu saat ini)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
            java.io.File serverFile = new java.io.File(uploadDir + fileName);
            
            // 3. Simpan file ke folder
            file.transferTo(serverFile);

            // 4. Kembalikan URL yang bisa diakses oleh frontend
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            return ResponseEntity.ok(fileUrl);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Gagal mengupload file: " + e.getMessage());
        }
    }
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
  // --- TAMBAHKAN KODE INI DI BAWAH CONTROLLER (Ganti yang sebelumnya) ---

    @Autowired
    private com.PBO2.CampShare.repository.BarangRepository barangRepository;

    @GetMapping("/user/{idUser}")
    public ResponseEntity<?> getBarangByUser(@PathVariable String idUser) {
        try {
            // 1. Ambil semua barang milik user ini sekaligus dari database
            List<Barang> semuaBarang = barangRepository.findByPemilikIdUser(idUser);

            // 2. Pisahkan mana yang Barang Jual dan mana yang Barang Pinjam untuk dikirim ke HTML
            List<Barang> jual = semuaBarang.stream()
                .filter(b -> b instanceof com.PBO2.CampShare.entity.BarangJual)
                .collect(java.util.stream.Collectors.toList());
                
            List<Barang> pinjam = semuaBarang.stream()
                .filter(b -> b instanceof com.PBO2.CampShare.entity.BarangPinjam)
                .collect(java.util.stream.Collectors.toList());

            // 3. Masukkan ke format response
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("barangJual", jual);
            response.put("barangPinjam", pinjam);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
}