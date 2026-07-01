package com.PBO2.CampShare.dto;

import com.PBO2.CampShare.entity.enumeration.StatusRequest;

public class RequestResponse {

    private Long id;
    private String namaBarang;
    private String deskripsi;
    private Integer anggaran;
    private String userId;
    private String acceptedByUserId;
    private String username;
    private StatusRequest statusRequest;

    // Constructor kosong
    public RequestResponse() {
    }

    // Getter dan Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Integer getAnggaran() {
        return anggaran;
    }

    public void setAnggaran(Integer anggaran) {
        this.anggaran = anggaran;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAcceptedByUserId() {
        return acceptedByUserId;
    }

    public void setAcceptedByUserId(String acceptedByUserId) {
        this.acceptedByUserId = acceptedByUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public StatusRequest getStatusRequest() {
        return statusRequest;
    }

    public void setStatusRequest(StatusRequest statusRequest) {
        this.statusRequest = statusRequest;
    }
}