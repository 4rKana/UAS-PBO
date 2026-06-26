package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import com.PBO2.CampShare.service.OtpService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService; // <-- Tambahkan ini

    // --- API BARU UNTUK KIRIM EMAIL OTP ---
    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            otpService.generateAndSendOtp(email);
            return ResponseEntity.ok(Map.of("message", "OTP berhasil dikirim ke email!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Gagal mengirim email: " + e.getMessage()));
        }
    }

    // --- API REGISTER DIUPDATE ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> requestData) {
        String email = (String) requestData.get("email");
        String otp = (String) requestData.get("otp");

        // 1. Validasi OTP terlebih dahulu
        if (!otpService.validateOtp(email, otp)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Gagal: Kode OTP salah atau kadaluarsa!"));
        }

        // 2. Jika OTP benar, pindahkan data ke object User
        User userRequest = new User();
        userRequest.setNim((String) requestData.get("nim"));
        userRequest.setAngkatan((String) requestData.get("angkatan"));
        userRequest.setUsername((String) requestData.get("username"));
        userRequest.setEmail(email);
        userRequest.setPassword((String) requestData.get("password"));

        // 3. Lanjutkan pendaftaran seperti biasa
        String result = userService.register(userRequest);
        if (result.startsWith("Gagal")) {
            return ResponseEntity.badRequest().body(Map.of("message", result));
        }
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginRequest, 
            jakarta.servlet.http.HttpSession session) { // <-- 1. Tambahkan parameter HttpSession di sini
        
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        String result = userService.login(email, password);
        
        if (result.startsWith("Gagal")) {
            // Ubah balasan error jadi format JSON {"message": "Gagal: Password salah!"}
            return ResponseEntity.status(401).body(Map.of("message", result));
        }
        
        // Jika sukses, ambil data User secara lengkap berdasarkan email
        User loggedInUser = userService.findByEmail(email); 
        
        // === 2. SIMPAN ID USER KE SESSION DI SINI ===
        // Sesuaikan 'getIdUser()' dengan nama method Getter ID yang ada di Class User milikmu (misal: getId() atau getIdUser())
        session.setAttribute("userId", loggedInUser.getIdUser()); 
        
        // Kembalikan objek User (Spring Boot otomatis menjadikannya JSON yang berisi idUser)
        return ResponseEntity.ok(loggedInUser);
    }
}