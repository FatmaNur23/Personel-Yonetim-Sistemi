package com.example.personellistesi.controller;
import com.example.personellistesi.model.IzinTuru;
import com.example.personellistesi.repo.IzinTuruRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/izin-turleri")
@CrossOrigin
public class IzinTuruController {

    @Autowired
    private IzinTuruRepository izinTuruRepository;

    @GetMapping
    public List<IzinTuru> tumunuGetir() {
        return izinTuruRepository.findAll();
    }
}

