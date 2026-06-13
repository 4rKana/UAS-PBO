package com.PBO2.CampShare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // WAJIB ADA: Memberitahu Spring Boot bahwa ini adalah Class Induk
public abstract class Barang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namaBarang;

    @Column(nullable = false)
    private String deskripsi;

    private String fotoUrl; // Link gambar barang

    // Nanti Orang ke-6 tinggal melanjutkan relasi ke User (Pemilik Barang)
    // dan menambahkan enum KategoriBarang di class anaknya.
}