package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin // Biar aman dari CORS Error yang kita bahas tadi!
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User userRequest) {
        
        // 1. Validasi: Cek apakah email sudah dipakai orang lain
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Gagal: Email sudah terdaftar!");
        }

        // 2. Simpan user baru ke database
        userRepository.save(userRequest);

        return ResponseEntity.ok().body("Sukses: User berhasil didaftarkan!");
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        // 1. Cari user berdasarkan email yang diketik
        return userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> {
                    // 2. Jika email ketemu, cocokkan passwordnya
                    if (user.getPassword().equals(loginRequest.getPassword())) {
                        return ResponseEntity.ok().body("Sukses: Selamat Datang, " + user.getNama() + "!");
                    } else {
                        return ResponseEntity.status(401).body("Gagal: Password salah!");
                    }
                })
                // 3. Jika email tidak terdaftar
                .orElse(ResponseEntity.status(404).body("Gagal: Email tidak ditemukan!"));
    }
}