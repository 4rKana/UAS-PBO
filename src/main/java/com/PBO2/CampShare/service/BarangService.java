package com.PBO2.CampShare.service;

import com.PBO2.CampShare.entity.Barang;
import com.PBO2.CampShare.repository.BarangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BarangService {

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
    public void deleteBarang(Long id) {
        barangRepository.deleteById(id);
    }
}