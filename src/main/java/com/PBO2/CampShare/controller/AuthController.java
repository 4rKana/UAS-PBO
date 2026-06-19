package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User userRequest) {
        String result = userService.register(userRequest);
        if (result.startsWith("Gagal")) {
            // Ubah balasan jadi format JSON {"message": "Gagal..."}
            return ResponseEntity.badRequest().body(Map.of("message", result));
        }
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        String result = userService.login(email, password);
        
        if (result.startsWith("Gagal")) {
            // Ubah balasan error jadi format JSON {"message": "Gagal: Password salah!"}
            return ResponseEntity.status(401).body(Map.of("message", result));
        }
        
        // Jika sukses, ambil data User secara lengkap berdasarkan email
        User loggedInUser = userService.findByEmail(email); 
        
        // Kembalikan objek User (Spring Boot otomatis menjadikannya JSON yang berisi idUser)
        return ResponseEntity.ok(loggedInUser);
    }
}