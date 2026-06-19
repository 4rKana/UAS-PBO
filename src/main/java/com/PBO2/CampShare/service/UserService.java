package com.PBO2.CampShare.service;

import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.entity.enumeration.Role;
import com.PBO2.CampShare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailValidationService emailValidationService;

    public String register(User user) {
        if (!emailValidationService.isValidEmail(user.getEmail())) {
            return "Gagal: Format email tidak valid!";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Gagal: Email sudah terdaftar!";
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Gagal: Username sudah digunakan!";
        }
        if (userRepository.existsByNim(user.getNim())) {
            return "Gagal: NIM sudah terdaftar!";
        }
        
        // --- JURUS ENKRIPSI PASSWORD DIMULAI ---
        String passwordEnkripsi = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(passwordEnkripsi);
        // --- JURUS ENKRIPSI SELESAI ---

        // Setiap user baru mendaftar, otomatis rolenya jadi USER biasa
        user.setRole(Role.USER); 
        userRepository.save(user);
        return "Sukses: User berhasil didaftarkan!";
    }

    public String login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Gagal: Email tidak ditemukan!";
        }
        
        User user = userOpt.get();
        
        // --- JURUS CEK PASSWORD ENKRIPSI DIMULAI ---
        // Penjelasan: BCrypt.checkpw(Password_Ketik, Password_Database)
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return "Gagal: Password salah!";
        }
        // --- JURUS CEK SELESAI ---

        return "Sukses: Selamat Datang, " + user.getUsername() + " (Role: " + user.getRole() + ")!";
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}