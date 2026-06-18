package com.PBO2.CampShare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.PBO2.CampShare.entity.RequestBarang;
import com.PBO2.CampShare.entity.enumeration.StatusRequest;
import com.PBO2.CampShare.repository.RequestRepository;

@Service
public class RequestService {
    
    @Autowired
    private RequestRepository requestRepository;

    public List<RequestBarang> getAllRequest() {
        return requestRepository.findAll();
    }

    public RequestBarang buatRequest(RequestBarang request) {
        request.setStatusRequest(StatusRequest.MENCARI);
        return requestRepository.save(request);
    }

    public RequestBarang ubahStatus(Long id, StatusRequest statusBaru) {
        RequestBarang request = requestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Request tidak ditemukan"));
        request.setStatusRequest(statusBaru);
        return requestRepository.save(request);
    }
}
