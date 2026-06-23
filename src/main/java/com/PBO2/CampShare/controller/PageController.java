package com.PBO2.CampShare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/detail-barang")
    public String detailBarang() {
        return "detail-barang";
    }

    @GetMapping("/form-barang")
    public String formBarang() {
        return "form-barang";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/notifikasi")
    public String notifikasi() {
        return "notifikasi";
    }

    @GetMapping("/papan-request")
    public String papanRequest() {
        return "papan-request";
    }

    @GetMapping("/profil")
    public String profil() {
        return "profil";
    }

    @GetMapping("/roomchat")
    public String roomchat() {
        return "roomchat";
    }

    @GetMapping("/transaksi")
    public String transaksi() {
        return "transaksi";
    }
}