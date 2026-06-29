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
    private String userId;
    
    @Column(name = "anggaran")
    private Integer anggaran;

    @Column(name = "accepted_by_user_id")
    private String acceptedByUserId;

    @Enumerated(EnumType.STRING)
    private StatusRequest statusRequest;


    // Constructor Kosong (Wajib untuk JPA)
    public RequestBarang() {}

    // Constructor dengan Parameter

    public RequestBarang(String namaBarang, String deskripsi, Integer anggaran, StatusRequest statusRequest, String userId, String acceptedByUserId) {

        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.anggaran = anggaran;
        this.statusRequest = statusRequest;
        this.userId = userId;
        this.acceptedByUserId = acceptedByUserId;
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

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAcceptedByUserId() { return acceptedByUserId; }
    public void setAcceptedByUserId(String acceptedByUserId) { this.acceptedByUserId = acceptedByUserId; }

}