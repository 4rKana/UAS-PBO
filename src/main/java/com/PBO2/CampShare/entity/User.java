package com.PBO2.CampShare.entity;

import com.PBO2.CampShare.entity.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Menggenerate otomatis ID berupa String unik UUID
    private String idUser;

    @Column(nullable = false, unique = true)
    private String nim;

    @Column(nullable = false)
    private String angkatan;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Tambahan deskripsi dengan tipe TEXT agar bisa menampung banyak karakter
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    
}