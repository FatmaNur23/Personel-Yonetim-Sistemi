package com.example.personellistesi.service;

import com.example.personellistesi.model.Departman;
import com.example.personellistesi.repo.DepartmanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmanService {

    @Autowired
    private DepartmanRepository departmanRepository;

    public List<Departman> tumDepartmanlariGetir() {
        return departmanRepository.findAll();
    }

    public Departman departmanGetir(String id) {
        return departmanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hata: " + id + " numaralı departman bulunamadı!"));
    }
}