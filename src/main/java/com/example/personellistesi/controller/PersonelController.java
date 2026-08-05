package com.example.personellistesi.controller;

import com.example.personellistesi.model.Personel;
import com.example.personellistesi.service.PersonelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/personeller")
@CrossOrigin(origins = "*") // CORS hatası almamak için bunu da ekledik
public class PersonelController {

    private final PersonelService personelService;

    @Autowired
    public PersonelController(PersonelService personelService) {
        this.personelService = personelService;
    }

    @GetMapping("/liste")
    public ResponseEntity<List<Personel>> tumunuListele() {
        try {
            List<Personel> personeller = personelService.tumunuGetir();
            return ResponseEntity.ok(personeller);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/sil/{id}")
    public ResponseEntity<String> sil(@PathVariable String id) {
        try {
            personelService.idIleSil(id);
            return ResponseEntity.ok("Personel başarıyla silindi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata oluştu: " + e.getMessage());
        }
    }

    @PostMapping("/excel-yukle")
    public ResponseEntity<String> excelYukle(@RequestParam("file") MultipartFile file) {
        try {
            String sonuc = personelService.excelImport(file);
            return ResponseEntity.ok(sonuc);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/excel-indir")
    public ResponseEntity<InputStreamResource> excelIndir() {
        try {
            ByteArrayInputStream in = personelService.exportToExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=personel_listesi.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> personelEkle(@RequestBody Personel personel) {
        try {
            Personel kaydedilen = personelService.personelEkle(personel);
            return ResponseEntity.ok(kaydedilen);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata oluştu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> personelGuncelle(@PathVariable String id, @RequestBody Personel personel) {
        try {
            Personel guncellenen = personelService.personelGuncelle(id, personel);
            return ResponseEntity.ok(guncellenen);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata oluştu: " + e.getMessage());
        }
    }
}









