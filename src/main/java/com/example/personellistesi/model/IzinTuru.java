package com.example.personellistesi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "izin_turu")
public class IzinTuru {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String ad;

    // Getter ve Setter metotlarını eklemeyi unutma (veya @Data kullanıyorsan lombok halleder)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
}
