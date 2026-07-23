package com.example.personellistesi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "izin")
public class Izin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private LocalDate baslangicTarihi;
    private LocalDate bitisTarihi;
    private String izinTuru;

    // Birden fazla izin tek bir personele ait olabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personel_id")
    private Personel personel;

    // Constructor, Getter ve Setter Metotları
    public Izin() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getBaslangicTarihi() { return baslangicTarihi; }
    public void setBaslangicTarihi(LocalDate baslangicTarihi) { this.baslangicTarihi = baslangicTarihi; }

    public LocalDate getBitisTarihi() { return bitisTarihi; }
    public void setBitisTarihi(LocalDate bitisTarihi) { this.bitisTarihi = bitisTarihi; }

    public String getIzinTuru() { return izinTuru; }
    public void setIzinTuru(String izinTuru) { this.izinTuru = izinTuru; }

    public Personel getPersonel() { return personel; }
    public void setPersonel(Personel personel) { this.personel = personel; }
}
