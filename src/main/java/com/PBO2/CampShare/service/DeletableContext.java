package com.PBO2.CampShare.service;

public interface DeletableContext {
    // Memaksa class yang memakai interface ini untuk memiliki method ini
    boolean isReadyToDelete();
}