package com.PBO2.CampShare.entity;

import com.PBO2.CampShare.entity.enumeration.KategoriBarang;
import com.PBO2.CampShare.entity.enumeration.StatusBarang;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "barang_pinjam")
public class BarangPinjam extends Barang {

    @Column(nullable = false)
    private Double tarifPinjam;

    @Column(nullable = false)
    private Integer durasiMaksimalHari;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KategoriBarang kategori;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBarang status;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "idUser") // Menghubungkan ke idUser bertipe String di entity User
    private User pemilik;
}