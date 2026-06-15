package com.PBO2.CampShare.repository;

import com.PBO2.CampShare.entity.TransaksiBeli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaksiBeliRepository extends JpaRepository<TransaksiBeli, String> {
}
