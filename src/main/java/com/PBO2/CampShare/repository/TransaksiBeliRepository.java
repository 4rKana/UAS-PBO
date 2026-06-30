package com.PBO2.CampShare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.PBO2.CampShare.entity.TransaksiBeli;
import com.PBO2.CampShare.entity.User;

@Repository
public interface TransaksiBeliRepository extends JpaRepository<TransaksiBeli, String> {
    
    // Method tambahan wajib: Untuk mencari riwayat transaksi berdasarkan user yang membeli
    List<TransaksiBeli> findByPembeli(User pembeli);

    @Query("SELECT t FROM TransaksiBeli t WHERE (t.pembeli.idUser = :u1 AND t.barang.pemilik.idUser = :u2) OR (t.pembeli.idUser = :u2 AND t.barang.pemilik.idUser = :u1)")
    List<TransaksiBeli> findTransaksiAntaraDuaUser(@org.springframework.data.repository.query.Param("u1") String u1, @org.springframework.data.repository.query.Param("u2") String u2);
}