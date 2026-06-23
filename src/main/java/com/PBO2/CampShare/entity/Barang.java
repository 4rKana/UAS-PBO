package com.PBO2.CampShare.entity;

import com.PBO2.CampShare.entity.enumeration.KategoriBarang;
import com.PBO2.CampShare.entity.enumeration.StatusBarang;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // 1. UBAH DARI @MappedSuperclass MENJADI @Entity
@Inheritance(strategy = InheritanceType.JOINED) // 2. TAMBAHKAN INI (Agar class induk-anak terhubung secara polimorfisme di DB)
public abstract class Barang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namaBarang;

    @Column(nullable = false)
    private String deskripsi;

    private String fotoUrl; // Link gambar barang

    // 👇 3. PINDAHKAN/TAMBAHKAN KEDUA VARIABEL INI KE KELAS INDUK 👇
    @Enumerated(EnumType.STRING)
    private KategoriBarang kategori;

    @Enumerated(EnumType.STRING)
    private StatusBarang status;
    // ... variabel status dan kategori ...

    // --- TAMBAHKAN INI DI BARANG.JAVA ---
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "idUser")
    private User pemilik;

}