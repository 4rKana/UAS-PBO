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
    public ResponseEntity<String> register(@RequestBody User userRequest) {
        String result = userService.register(userRequest);
        if (result.startsWith("Gagal")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        String result = userService.login(loginRequest.get("email"), loginRequest.get("password"));
        if (result.startsWith("Gagal")) {
            return ResponseEntity.status(401).body(result);
        }
        return ResponseEntity.ok(result);
    }
}