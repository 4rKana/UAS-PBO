package com.PBO2.CampShare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.PBO2.CampShare.entity.TransaksiPinjam;
import java.util.List;

public interface TransaksiPinjamRepository extends JpaRepository<TransaksiPinjam, String> {
    
    // Asumsi nama variabel User di TransaksiPinjam adalah 'peminjam'. 
    // (Jika namanya 'pembeli' seperti di TransaksiBeli, ganti tulisan 'Peminjam' di bawah menjadi 'Pembeli')
    List<TransaksiPinjam> findByPeminjamIdUser(String idUser);
}