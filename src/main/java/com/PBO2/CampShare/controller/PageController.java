package com.PBO2.CampShare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Saat user buka /login, tampilkan file login.html
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Saat user buka /register, tampilkan file register.html
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Saat user buka /dashboard, tampilkan file dashboard.html
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }
}