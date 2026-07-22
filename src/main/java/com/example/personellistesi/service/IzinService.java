package com.example.personellistesi.service;

import com.example.personellistesi.model.Izin;
import com.example.personellistesi.model.Personel;import com.example.personellistesi.repo.IzinRepository;
import com.example.personellistesi.repo.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IzinService {
    @Autowired
    private IzinRepository izinRepository;

    @Autowired
    private PersonelRepository personelRepository;

    // ─── 1. YENİ İZİN EKLEME ───
    @Transactional
    public Izin izinEkle(Izin izin) {
        validasyonlariKontrolEt(izin);

        // Personel gerçekten var mı kontrolü
        Personel personel = personelRepository.findById(izin.getPersonel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Hata: Geçersiz Personel ID! Sistemde böyle bir personel bulunamadı."));

        izin.setPersonel(personel);
        return izinRepository.save(izin);
    }

    // ─── 2. BELİRLİ BİR PERSONELİN İZİNLERİNİ GETİRME ───
    public List<Izin> personeleAitIzinleriGetir(String personelId) {
        // Personel varlık kontrolü
        if (!personelRepository.existsById(personelId)) {
            throw new IllegalArgumentException("Hata: " + personelId + " ID'li personel bulunamadı!");
        }
        return izinRepository.findByPersonelId(personelId);
    }

    // ─── 3. İZİN GÜNCELLEME ───
    @Transactional
    public Izin izinGuncelle(String id, Izin guncelIzin) {
        Izin mevcutIzin = izinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hata: Güncellenmek istenen izin kaydı bulunamadı!"));

        validasyonlariKontrolEt(guncelIzin);

        mevcutIzin.setBaslangicTarihi(guncelIzin.getBaslangicTarihi());
        mevcutIzin.setBitisTarihi(guncelIzin.getBitisTarihi());
        mevcutIzin.setIzinTuru(guncelIzin.getIzinTuru());

        return izinRepository.save(mevcutIzin);
    }

    // ─── 4. İZİN SİLME ───
    @Transactional
    public void izinSil(String id) {
        if (!izinRepository.existsById(id)) {
            throw new IllegalArgumentException("Hata: Silinmek istenen izin kaydı bulunamadı!");
        }
        izinRepository.deleteById(id);
    }

    // ─── ORTAK VALİDASYON METODU ───
    private void validasyonlariKontrolEt(Izin izin) {
        if (izin.getIzinTuru() == null || izin.getIzinTuru().trim().isEmpty()) {
            throw new IllegalArgumentException("Hata: İzin türü boş bırakılamaz!");
        }
        if (izin.getBaslangicTarihi() == null || izin.getBitisTarihi() == null) {
            throw new IllegalArgumentException("Hata: Başlangıç ve bitiş tarihleri boş bırakılamaz!");
        }
        if (izin.getBaslangicTarihi().isAfter(izin.getBitisTarihi())) {
            throw new IllegalArgumentException("Hata: Başlangıç tarihi, bitiş tarihinden sonra olamaz!");
        }
        if (izin.getPersonel() == null || izin.getPersonel().getId() == null) {
            throw new IllegalArgumentException("Hata: İzin atanacak personel ID'si eksik!");
        }
    }
}

