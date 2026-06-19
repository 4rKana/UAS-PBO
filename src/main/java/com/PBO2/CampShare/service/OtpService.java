package com.PBO2.CampShare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // <-- TAMBAHAN IMPORT BARU
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    // 👉 Mengambil otomatis email Gmail kamu yang ada di application.properties
    @Value("${spring.mail.username}")
    private String senderEmail;

    // Menyimpan OTP sementara di memori server (Email -> OTP)
    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public void generateAndSendOtp(String email) {
        // Buat 6 angka acak
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Simpan ke memori
        otpStorage.put(email, otp);

        // Kirim email
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom(senderEmail); // 🔥 BARIS SAKTI: Mengunci email pengirim agar tidak error lagi!
        message.setTo(email);
        message.setSubject("Kode OTP Pendaftaran CampShare");
        message.setText("Halo!\n\nKode OTP Anda adalah: " + otp + "\n\nJangan berikan kode ini kepada siapapun.");
        
        mailSender.send(message);
    }

    public boolean validateOtp(String email, String otp) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            otpStorage.remove(email); // Hapus OTP setelah sukses dipakai
            return true;
        }
        return false;
    }
}