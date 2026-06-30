package com.PBO2.CampShare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.PBO2.CampShare.entity.TransaksiPinjam;

public interface TransaksiPinjamRepository extends JpaRepository<TransaksiPinjam, String> {
    
    // Asumsi nama variabel User di TransaksiPinjam adalah 'peminjam'. 
    // (Jika namanya 'pembeli' seperti di TransaksiBeli, ganti tulisan 'Peminjam' di bawah menjadi 'Pembeli')
    List<TransaksiPinjam> findByPeminjamIdUser(String idUser);

    @Query("SELECT t FROM TransaksiPinjam t WHERE (t.peminjam.idUser = :u1 AND t.barang.pemilik.idUser = :u2) OR (t.peminjam.idUser = :u2 AND t.barang.pemilik.idUser = :u1)")
    List<TransaksiPinjam> findTransaksiAntaraDuaUser(@org.springframework.data.repository.query.Param("u1") String u1, @org.springframework.data.repository.query.Param("u2") String u2);
}