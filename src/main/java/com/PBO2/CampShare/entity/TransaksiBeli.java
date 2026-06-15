package com.PBO2.CampShare.entity;

import com.PBO2.CampShare.entity.enumeration.StatusBarang;
import com.PBO2.CampShare.entity.enumeration.StatusTransaksiBeli;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TransaksiBeli implements DeletableContext {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idTransaksi;

    @ManyToOne
    @JoinColumn(name = "id_pembeli")
    private User pembeli;

    @ManyToOne
    @JoinColumn(name = "id_barang")
    private BarangJual barang;

    @Enumerated(EnumType.STRING)
    private StatusTransaksiBeli status;

    public void prosesBooking() {
        this.status = StatusTransaksiBeli.DISETUJUI;
        if(this.barang != null) {
            this.barang.ubahStatus(StatusBarang.booked);
        }
    }

    public void konfirmasiSelesai() {
        this.status = StatusTransaksiBeli.SELESAI;
        if(this.barang != null) {
            this.barang.ubahStatus(StatusBarang.sold);
        }
    }

    @Override
    public boolean isReadyToDelete() {
        return this.status == StatusTransaksiBeli.SELESAI;
    }
}