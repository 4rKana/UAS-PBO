package com.PBO2.CampShare.repository;

import com.PBO2.CampShare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Fungsi bawaan untuk mengecek apakah email sudah terdaftar atau belum
    boolean existsByEmail(String email);
    // Tambahkan baris ini di bawah existsByEmail
    java.util.Optional<User> findByEmail(String email);
}