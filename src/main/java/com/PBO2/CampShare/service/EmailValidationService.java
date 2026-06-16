package com.PBO2.CampShare.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class EmailValidationService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }
}