package com.PBO2.CampShare.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoomChatController {

    @GetMapping("/roomchat")
    public String viewRoomChat(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "roomchat";
    }
}