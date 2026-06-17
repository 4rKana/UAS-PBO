package com.PBO2.CampShare.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.PBO2.CampShare.entity.enumeration.StatusTransaksiPinjam;

import jakarta.persistence.*;

@Entity
public class TransaksiPinjam {

    @Id
    private String idTransaksi;

    @ManyToOne
    private User peminjam;

    @ManyToOne
    private BarangPinjam barang;

    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;

    @Enumerated(EnumType.STRING)
    private StatusTransaksiPinjam status;

    public TransaksiPinjam() {
    }

    public double hitungTotalTarif() {
        long jumlahHari = ChronoUnit.DAYS.between(tanggalMulai, tanggalSelesai);
        return jumlahHari * barang.getTarifPinjam();
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

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public User getPeminjam() {
        return peminjam;
    }

    public void setPeminjam(User peminjam) {
        this.peminjam = peminjam;
    }

    public BarangPinjam getBarang() {
        return barang;
    }

    public void setBarang(BarangPinjam barang) {
        this.barang = barang;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public LocalDate getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(LocalDate tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public StatusTransaksiPinjam getStatus() {
        return status;
    }

    public void setStatus(StatusTransaksiPinjam status) {
        this.status = status;
    }
}