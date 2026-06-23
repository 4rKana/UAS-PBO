package com.PBO2.CampShare.repository;

import com.PBO2.CampShare.entity.Barang;
import com.PBO2.CampShare.entity.enumeration.KategoriBarang;
import com.PBO2.CampShare.entity.enumeration.StatusBarang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarangRepository extends JpaRepository<Barang, Long> {
    
    // Mengambil semua barang khusus yang dijual
    @Query("SELECT bj FROM BarangJual bj")
    List<Barang> findAllBarangJual();

    // Mengambil semua barang khusus yang dipinjamkan/disewakan
    @Query("SELECT bp FROM BarangPinjam bp")
    List<Barang> findAllBarangPinjam();

    // Mencari barang berdasarkan kategori
    List<Barang> findByKategori(KategoriBarang kategori);

    // Mencari barang berdasarkan statusnya
    List<Barang> findByStatus(StatusBarang status);

    // --- TAMBAHKAN BARIS INI ---
    // Mencari semua barang (baik jual maupun pinjam) milik satu user
    List<Barang> findByPemilikIdUser(String idUser);
}
