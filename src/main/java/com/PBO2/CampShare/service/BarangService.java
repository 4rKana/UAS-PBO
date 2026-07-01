package com.PBO2.CampShare.service;

import com.PBO2.CampShare.entity.Barang;
import com.PBO2.CampShare.entity.BarangJual;
import com.PBO2.CampShare.entity.enumeration.StatusBarang;
import com.PBO2.CampShare.repository.BarangRepository;
import com.PBO2.CampShare.repository.TransaksiBeliRepository;
import com.PBO2.CampShare.repository.TransaksiPinjamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@Service
public class BarangService {
    @Autowired
    private TransaksiBeliRepository transaksiBeliRepository;

    @Autowired
    private TransaksiPinjamRepository transaksiPinjamRepository;

    @Autowired
    private BarangRepository barangRepository;

    // Menyimpan barang (bisa BarangJual atau BarangPinjam)
    public Barang saveBarang(Barang barang) {
        return barangRepository.save(barang);
    }

    // Ambil semua barang untuk katalog utama
    public List<Barang> getAllBarang() {
        return barangRepository.findAll();
    }

    // Ambil khusus barang yang dijual
    public List<Barang> getAllBarangJual() {
        return barangRepository.findAllBarangJual();
    }

    // Ambil khusus barang yang dipinjam
    public List<Barang> getAllBarangPinjam() {
        return barangRepository.findAllBarangPinjam();
    }

    // Cari barang berdasarkan ID
    public Optional<Barang> getBarangById(Long id) {
        return barangRepository.findById(id);
    }

    // Hapus barang dari database
    public void deleteBarang(Long id, String userId) {
        Barang barang = barangRepository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Barang tidak ditemukan"));

        if (!barang.getPemilik().getIdUser().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Anda bukan pemilik barang.");
        }

        if (barang.getStatus() != StatusBarang.available) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barang tidak dapat dihapus.");
        }

        boolean adaTransaksi;

        if (barang instanceof BarangJual) {
            adaTransaksi = transaksiBeliRepository.existsByBarang_Id(id);
        } else {
            adaTransaksi = transaksiPinjamRepository.existsByBarang_Id(id);
        }

        if (adaTransaksi) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Barang sudah memiliki transaksi.");
        }

        barangRepository.delete(barang);
    }
}