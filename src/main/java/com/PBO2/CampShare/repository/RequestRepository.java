package com.PBO2.CampShare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.PBO2.CampShare.entity.RequestBarang;

@Repository
public interface RequestRepository extends JpaRepository<RequestBarang, Long> {
    
}
