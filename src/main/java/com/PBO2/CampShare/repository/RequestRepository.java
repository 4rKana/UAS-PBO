package com.PBO2.CampShare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<RequestBarang, Long> {
    
     // UBAH: Gunakan Enum StatusRequest agar type-safe dan sesuai dengan Entity
    List<RequestBarang> findByStatusRequest(StatusRequest statusRequest);
}
