package com.PBO2.CampShare.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "barang_jual")
public class BarangJual extends Barang {

    @Column(nullable = false)
    private Double harga;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "idUser") // Menghubungkan ke idUser bertipe String di entity User
    private User pemilik;
}