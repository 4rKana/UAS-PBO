package com.PBO2.CampShare.controller;

import com.PBO2.CampShare.dto.UpdateBioRequest;
import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.service.UserService;
import com.PBO2.CampShare.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest,
                                    HttpServletRequest request,
                                    HttpServletResponse httpResponse) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        String result = userService.login(email, password);
        if (result.startsWith("Gagal")) {
            return ResponseEntity.status(401).body(Map.of("message", result));
        }

        User loggedInUser = userService.findByEmail(email);

        // 1. Buat objek Authentication
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                loggedInUser.getEmail(), null,
                // kalau belum pakai roles, bisa kosongkan list authorities
                java.util.Collections.emptyList()
            );

        // 2. Set ke SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);

        // 3. Simpan context ke session (ini kunci agar request berikutnya dianggap login)
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setAttribute("userId", loggedInUser.getIdUser());

        Map<String, Object> response = new HashMap<>();
        response.put("idUser", loggedInUser.getIdUser());
        response.put("username", loggedInUser.getUsername());
        response.put("email", loggedInUser.getEmail());

        return ResponseEntity.ok(response);
    }
    @PutMapping("/user/{id}/update-bio")
    public ResponseEntity<?> updateBio(@PathVariable String id, @RequestBody UpdateBioRequest request) {
        // Perhatikan: Tipe data id diubah menjadi String menyesuaikan Entity milikmu
        return userRepository.findById(id).map(user -> {
            user.setDeskripsi(request.getDeskripsi());
            userRepository.save(user); // Simpan perubahan ke database
            return ResponseEntity.ok().body(Map.of("message", "Deskripsi berhasil diperbarui"));
        }).orElse(ResponseEntity.notFound().build());
    }
}