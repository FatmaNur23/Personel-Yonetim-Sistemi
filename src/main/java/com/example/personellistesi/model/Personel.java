package com.example.personellistesi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "personeller")
public class Personel {

    @Id
    @Column(length = 64)
    private String id;

    @Column(length = 11, unique = true, nullable = false)
    private String tckn;

    @Column(length = 128, nullable = false)
    private String ad;

    @Column(length = 128, nullable = false)
    private String soyad;

    @Column(length = 63)
    private String telefon;

    private Integer yas;

    @Column(name = "maaş")
    private Float maas;

    @Column(name = "işe_giriş_tarihi", nullable = false)
    private LocalDate iseGirisTarihi;

    @Column(name = "Kart_son_güncelleme")
    private LocalDateTime kartSonGuncelleme;

    // Entity kaydedilmeden veya güncellenmeden hemen önce çalışır
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString(); // Yeni kayıt için UUID üretiyoruz
        }
        this.kartSonGuncelleme = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.kartSonGuncelleme = LocalDateTime.now();
    }

    // Getter ve Setter Metotları (veya Lombok @Data kullanabilirsin)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTckn() { return tckn; }
    public void setTckn(String tckn) { this.tckn = tckn; }
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public Integer getYas() { return yas; }
    public void setYas(Integer yas) { this.yas = yas; }
    public Float getMaas() { return maas; }
    public void setMaas(Float maas) { this.maas = maas; }
    public LocalDate getIseGirisTarihi() { return iseGirisTarihi; }
    public void setIseGirisTarihi(LocalDate iseGirisTarihi) { this.iseGirisTarihi = iseGirisTarihi; }
    public LocalDateTime getKartSonGuncelleme() { return kartSonGuncelleme; }
    public void setKartSonGuncelleme(LocalDateTime kartSonGuncelleme) { this.kartSonGuncelleme = kartSonGuncelleme; }

    // Personelin bağlı olduğu Departman
    @NotNull(message = "Personel kayıt edilirken departman bilgisi boş bırakılamaz!")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departman_id")
    private Departman departman;

    // Personele ait İzinler Listesi
    @OneToMany(mappedBy = "personel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // JSON dönüşünde sonsuz döngüye (Infinite Loop) girmemesi için
    private List<Izin> izinler;

    // ─── YENİ GETTER VE SETTER METOTLARI ───

    public Departman getDepartman() { return departman; }
    public void setDepartman(Departman departman) { this.departman = departman; }

    public List<Izin> getIzinler() { return izinler; }
    public void setIzinler(List<Izin> izinler) { this.izinler = izinler; }
}


