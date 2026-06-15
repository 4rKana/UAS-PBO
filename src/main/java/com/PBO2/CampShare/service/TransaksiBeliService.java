package com.PBO2.CampShare.service;

import com.PBO2.CampShare.entity.BarangJual;
import com.PBO2.CampShare.entity.TransaksiBeli;
import com.PBO2.CampShare.entity.User;
import com.PBO2.CampShare.entity.enumeration.StatusTransaksiBeli;
import com.PBO2.CampShare.repository.TransaksiBeliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransaksiBeliService {

    @Autowired
    private TransaksiBeliRepository repository;

    @Transactional
    public TransaksiBeli buatTransaksiBaru(User pembeli, BarangJual barang) {
        TransaksiBeli transaksi = new TransaksiBeli();
        transaksi.setPembeli(pembeli);
        transaksi.setBarang(barang);
        transaksi.setStatus(StatusTransaksiBeli.DIAJUKAN);
        return repository.save(transaksi);
    }

    @Transactional
    public TransaksiBeli prosesBooking(String idTransaksi) {
        TransaksiBeli transaksi = repository.findById(idTransaksi)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan!"));
        transaksi.prosesBooking();
        return repository.save(transaksi);
    }

    @Transactional
    public TransaksiBeli konfirmasiSelesai(String idTransaksi) {
        TransaksiBeli transaksi = repository.findById(idTransaksi)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan!"));
        transaksi.konfirmasiSelesai();
        return repository.save(transaksi);
    }

    public List<TransaksiBeli> getAllTransaksi() {
        return repository.findAll();
    }
}