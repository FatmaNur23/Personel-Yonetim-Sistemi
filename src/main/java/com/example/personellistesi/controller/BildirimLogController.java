package com.example.personellistesi.controller;

import com.example.personellistesi.model.BildirimLog;
import com.example.personellistesi.service.IBildirimLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bildirim-loglari")
@CrossOrigin(origins = "*") // CORS hatası almamak için
public class BildirimLogController {

    private  IBildirimLogService bildirimLogService;

    // Dependency Injection doğrudan Interface üzerinden yapılıyor
    @Autowired
    public BildirimLogController(IBildirimLogService bildirimLogService) {
        this.bildirimLogService = bildirimLogService;
    }

    // ─── GET API: Tüm Bildirim Loglarını Listeler (Önyüzdeki tablo için)
    @GetMapping
    public ResponseEntity<List<BildirimLog>> tumLoglariListele() {
        try {
            List<BildirimLog> loglar = bildirimLogService.getAllLogs();
            return ResponseEntity.ok(loglar);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ─── GET API: ID'ye göre tekil log getirir
    @GetMapping("/{id}")
    public ResponseEntity<?> idIleGetir(@PathVariable String id) {
        try {
            BildirimLog log = bildirimLogService.getLogById(id);
            return ResponseEntity.ok(log);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─── GET API: Belirli bir e-posta adresine giden logları filtreler
    // Örnek kullanım: /api/bildirim-loglari/alici?email=ahmet@ornek.com
    @GetMapping("/alici")
    public ResponseEntity<List<BildirimLog>> aliciyaGoreFiltrele(@RequestParam("email") String toAddress) {
        try {
            List<BildirimLog> loglar = bildirimLogService.getLogsByToAddress(toAddress);
            return ResponseEntity.ok(loglar);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ─── POST API: Manuel olarak log ekleme
    @PostMapping
    public ResponseEntity<?> logEkle(@RequestBody BildirimLog bildirimLog) {
        try {
            BildirimLog kaydedilenLog = bildirimLogService.saveLog(bildirimLog);
            return ResponseEntity.status(HttpStatus.CREATED).body(kaydedilenLog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata oluştu: " + e.getMessage());
        }
    }
}
