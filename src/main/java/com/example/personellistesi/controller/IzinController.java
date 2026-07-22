package com.example.personellistesi.controller;

import com.example.personellistesi.model.Izin;
import com.example.personellistesi.service.IzinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class IzinController {

    @Autowired
    private IzinService izinService;

    public IzinController(IzinService izinService) {
        this.izinService = izinService;
    }

    // POST /api/izinler — Yeni izin ekleme
    @PostMapping("/izinler")
    public ResponseEntity<?> izinEkle(@RequestBody Izin izin) {
        try {
            Izin kaydedilenIzin = izinService.izinEkle(izin);
            return ResponseEntity.ok(kaydedilenIzin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata: " + e.getMessage());
        }
    }

    // GET /api/personeller/{personelId}/izinler — Belirli bir personelin tüm izinleri
    @GetMapping("/personeller/{personelId}/izinler")
    public ResponseEntity<?> personeleAitIzinleriGetir(@PathVariable String personelId) {
        try {
            List<Izin> izinler = izinService.personeleAitIzinleriGetir(personelId);
            return ResponseEntity.ok(izinler);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/izinler/{id} — Mevcut izni güncelleme
    @PutMapping("/izinler/{id}")
    public ResponseEntity<?> izinGuncelle(@PathVariable String id, @RequestBody Izin izin) {
        try {
            Izin guncellenenIzin = izinService.izinGuncelle(id, izin);
            return ResponseEntity.ok(guncellenenIzin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata: " + e.getMessage());
        }
    }

    // DELETE /api/izinler/{id} — İzin silme
    @DeleteMapping("/izinler/{id}")
    public ResponseEntity<?> izinSil(@PathVariable String id) {
        try {
            izinService.izinSil(id);
            return ResponseEntity.ok("İzin kaydı başarıyla silindi.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata: " + e.getMessage());
        }
    }
}
