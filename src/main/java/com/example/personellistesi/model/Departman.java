package com.example.personellistesi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "departman")
public class Departman {
    @Id
    private String id;

    @NotNull(message = "Departman adı boş bırakılamaz")
    @Column(nullable = false)
    private String ad;

    // Bir departmanda birden fazla personel bulunabilir
    @OneToMany(mappedBy = "departman", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Personel> personeller;

    // Constructor, Getter ve Setter Metotları
    public Departman() {}

    public Departman(String ad) {
        this.ad = ad;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public List<Personel> getPersoneller() { return personeller; }
    public void setPersoneller(List<Personel> personeller) { this.personeller = personeller; }
}
