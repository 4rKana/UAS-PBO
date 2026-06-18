package com.PBO2.CampShare.repository;

import com.PBO2.CampShare.entity.TransaksiBeli;
import com.PBO2.CampShare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaksiBeliRepository extends JpaRepository<TransaksiBeli, String> {
    
    // Method tambahan wajib: Untuk mencari riwayat transaksi berdasarkan user yang membeli
    List<TransaksiBeli> findByPembeli(User pembeli);
}