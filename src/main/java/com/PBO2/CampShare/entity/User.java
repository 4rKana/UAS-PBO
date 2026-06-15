package com.PBO2.CampShare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Nama tabel yang akan muncul di phpMyAdmin
public class User {

    @Id // Menandakan ini adalah Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Agar ID nambah otomatis (1, 2, 3, dst)
    private Long id;

    @Column(nullable = false) // Kolom nama, tidak boleh kosong
    private String nama;

    @Column(nullable = false, unique = true) // Email tidak boleh kosong dan tidak boleh ada yang kembar
    private String email;

    @Column(nullable = false) // Password tidak boleh kosong
    private String password;

    @Column(name = "no_hp") // Kolom untuk nomor HP
    private String noHp;

    // --- WAJIB ADA: Constructor Kosong ---
    public User() {
    }

    // --- GETTER DAN SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }
}