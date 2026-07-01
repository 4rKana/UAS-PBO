package com.PBO2.CampShare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.PBO2.CampShare.entity.TransaksiPinjam;
import com.PBO2.CampShare.repository.TransaksiPinjamRepository;

@Service
public class TransaksiPinjamService {

    private final TransaksiPinjamRepository repository;

    public TransaksiPinjamService(
            TransaksiPinjamRepository repository) {
        this.repository = repository;
    }

    public TransaksiPinjam create(
            TransaksiPinjam transaksi) {

        transaksi.ajukanPinjaman();

        return repository.save(transaksi);
    }

    public List<TransaksiPinjam> getAll() {
        return repository.findAll();
    }

    public TransaksiPinjam getById(String id) {
        return repository.findById(id)
                .orElseThrow();
    }

    public TransaksiPinjam updateStatus(
            String id,
            TransaksiPinjam transaksiBaru) {

        TransaksiPinjam transaksi =
                getById(id);

        transaksi.setStatus(
                transaksiBaru.getStatus());

        return repository.save(transaksi);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}