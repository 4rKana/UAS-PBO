package com.PBO2.CampShare.entity;

import com.PBO2.CampShare.entity.enumeration.StatusBarang;
import com.PBO2.CampShare.entity.enumeration.StatusTransaksiBeli;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class TransaksiBeli {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idTransaksi;

    @ManyToOne
    @JoinColumn(name = "id_pembeli")
    private User pembeli;

    @ManyToOne
    @JoinColumn(name = "id_barang")
    private BarangJual barang;

    @Enumerated(EnumType.STRING)
    private StatusTransaksiBeli status;

    @Column(name = "waktu_selesai")
    private java.time.LocalDateTime waktuSelesai;

   public void prosesBooking() {
        this.status = StatusTransaksiBeli.DISETUJUI;
        if(this.barang != null) {
            // Ganti ubahStatus menjadi setStatus
            this.barang.setStatus(StatusBarang.booked);
        }
    }

    public void konfirmasiSelesai() {
        this.status = StatusTransaksiBeli.SELESAI;
        this.waktuSelesai = java.time.LocalDateTime.now();
        if(this.barang != null) {
            this.barang.setStatus(StatusBarang.sold);
        }
    }
}