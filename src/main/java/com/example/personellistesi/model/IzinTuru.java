package com.example.personellistesi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "izin_turu")
public class IzinTuru {
    @Id
    private String id;

    @Column(nullable = false)
    private String ad;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
}
