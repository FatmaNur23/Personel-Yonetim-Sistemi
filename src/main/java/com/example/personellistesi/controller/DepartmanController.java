package com.example.personellistesi.controller;

import com.example.personellistesi.model.Departman;
import com.example.personellistesi.service.DepartmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departmanlar")
public class DepartmanController {

    @Autowired
    private DepartmanService departmanService;

    @GetMapping
    public ResponseEntity<List<Departman>> tumunuListele() {
        return ResponseEntity.ok(departmanService.tumDepartmanlariGetir());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departman> idIleGetir(@PathVariable String id) {
        try {
            return ResponseEntity.ok(departmanService.departmanGetir(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
