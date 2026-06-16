package com.PBO2.CampShare.entity;

import com.PBO2.CampShare.entity.enumeration.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Menggenerate otomatis ID berupa String unik
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
    private Role role; // Menghubungkan ke file Role.java tadi

    // Constructor Kosong
    public User() {}

    // Getter dan Setter (Sesuai dengan blueprint getEmail, dll)
    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getAngkatan() { return angkatan; }
    public void setAngkatan(String angkatan) { this.angkatan = angkatan; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}