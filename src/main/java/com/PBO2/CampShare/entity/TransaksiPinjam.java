package com.PBO2.CampShare.entity;

import java.time.LocalDate;

import com.PBO2.CampShare.entity.enumeration.StatusTransaksiPinjam;

import jakarta.persistence.*;

@Entity
public class TransaksiPinjam {

    @Id
    private String idTransaksi;

    @ManyToOne
    private User peminjam;

    @ManyToOne
    private BarangPinjam barang;    // BarangPinjam.java buatan arip

    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;

    @Enumerated(EnumType.STRING)
    private StatusTransaksiPinjam status;

    public TransaksiPinjam() {
    }

    public double hitungTotalTarif() {
        long jumlahHari =
                tanggalMulai.until(tanggalSelesai).getDays();

        return jumlahHari * barang.getTarifPerHari();
    }

    public void ajukanPinjaman() {
        this.status = StatusTransaksiPinjam.DIAJUKAN;
    }

    public void konfirmasiPengembalian() {
        this.status = StatusTransaksiPinjam.SELESAI;
    }

    public boolean isReadyToDelete() {
        return status == StatusTransaksiPinjam.SELESAI;
    }

    // Getter Setter
}