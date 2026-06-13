package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.dto.WebResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    // 1. Uji coba method GET (Mengambil data sederhana)
    @GetMapping("/ping")
    public WebResponse<String> ping() {
        return WebResponse.<String>builder()
                .code(200)
                .status("OK")
                .data("Mesin CampShare menyala! Laporan diterima, Komandan!")
                .build();
    }

    // 2. Uji coba method POST (Mengirim data JSON dari Postman)
    @PostMapping("/daftar-dummy")
    public WebResponse<String> daftarDummy(@RequestBody Map<String, String> requestData) {
        String namaUser = requestData.get("nama");
        
        return WebResponse.<String>builder()
                .code(200)
                .status("CREATED")
                .data("Registrasi bohongan sukses untuk: " + namaUser)
                .build();
    }
}