package com.PBO2.CampShare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // 1. Agar AuthController bisa memicu proses cek password
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Matikan CSRF untuk mempermudah API
            .cors(Customizer.withDefaults())
            
            .authorizeHttpRequests(auth -> auth
                // Pastikan /api/auth/** masuk di permitAll
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/api/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/dashboard/**", "/detail-barang/**", "/form-barang/**", "/notifikasi/**", "/papan-request/**", "/profil/**", "/roomchat/**", "/transaksi/**").authenticated()
                .anyRequest().authenticated() 
            )

            .formLogin(form -> form
                .disable()
            )

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    String acceptHeader = request.getHeader("Accept");
                    String requestedWith = request.getHeader("X-Requested-With");
                    
                    if ((acceptHeader != null && acceptHeader.contains("application/json")) || "XMLHttpRequest".equals(requestedWith)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"success\": false, \"message\": \"Gagal: Anda belum login atau sesi telah habis!\"}");
                    } else {
                        response.sendRedirect("/login");
                    }
                })
            );
            
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        
        config.addAllowedOrigin("http://localhost:8080"); 
        
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}