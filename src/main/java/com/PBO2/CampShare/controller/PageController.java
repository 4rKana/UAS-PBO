package com.PBO2.CampShare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Saat user buka /dashboard, tampilkan file dashboard.html
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    @GetMapping("/detail-barang")
    public String detail_barangPage() {
        return "detail-barang";
    }

    @GetMapping("/form-barang")
    public String form_barangPage() {
        return "form-barang";
    }

    // Saat user buka /login, tampilkan file login.html
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/notifikasi")
    public String notifikasiPage() {
        return "notifikasi";
    }

    @GetMapping("/papan-request")
    public String papan_requestPage() {
        return "papan-request";
    }

    @GetMapping("/profil")
    public String profilPage() {
        return "profil";
    }

    // Saat user buka /register, tampilkan file register.html
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/roomchat")
    public String roomchatPage() {
        return "roomchat";
    }

        @GetMapping("/transaksi")
    public String transaksiPage() {
        return "transaksi";
    }
}