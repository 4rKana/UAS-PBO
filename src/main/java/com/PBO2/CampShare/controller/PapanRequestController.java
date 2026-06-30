package com.PBO2.CampShare.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PapanRequestController {

    @GetMapping("/papan-request")
    public String viewPapanRequest(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "papan-request";
    }
}