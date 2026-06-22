package com.PBO2.CampShare.entity;

import com.PBO2.CampShare.entity.enumeration.StatusRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "request_barang")
public class RequestBarang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namaBarang;
    private String deskripsi;
    
    @Column(name = "anggaran")
    private Integer anggaran;

    @Enumerated(EnumType.STRING)
    private StatusRequest statusRequest;

    // Constructor Kosong (Wajib untuk JPA)
    public RequestBarang() {}

    // Constructor dengan Parameter
    public RequestBarang(String namaBarang, String deskripsi, Integer anggaran, StatusRequest statusRequest) {
        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.anggaran = anggaran;
        this.statusRequest = statusRequest;
    }

    // Getter dan Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public Integer getAnggaran() { return anggaran; }
    public void setAnggaran(Integer anggaran) { this.anggaran = anggaran; }

    public StatusRequest getStatusRequest() { return statusRequest; }
    public void setStatusRequest(StatusRequest statusRequest) { this.statusRequest = statusRequest; }
}