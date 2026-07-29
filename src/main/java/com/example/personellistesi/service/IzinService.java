package com.example.personellistesi.service;

import com.example.personellistesi.model.BildirimLog;
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

    @Autowired
    private IBildirimLogService bildirimLogService;

    // ─── 1. YENİ İZİN EKLEME ───
    @Transactional
    public Izin izinEkle(Izin izin) {
        validasyonlariKontrolEt(izin);

        // Personel gerçekten var mı kontrolü
        Personel personel = personelRepository.findById(izin.getPersonel().getId())
                .orElseThrow(() -> new IllegalArgumentException("Hata: Geçersiz Personel ID! Sistemde böyle bir personel bulunamadı."));

        izin.setPersonel(personel);
        Izin kaydedilenIzin = izinRepository.save(izin);
        if (personel.getEmail() != null) {
            BildirimLog log = new BildirimLog(
                    "Yeni İzin Talebiniz İşlendi",
                    "Sayın " + personel.getAd() + " " + personel.getSoyad() + ", \n" +
                            kaydedilenIzin.getBaslangicTarihi() + " ile " + kaydedilenIzin.getBitisTarihi() +
                            " tarihleri arasındaki izin talebiniz sisteme başarıyla işlenmiştir.",
                    personel.getEmail()
            );
            bildirimLogService.saveLog(log);
        }
        return kaydedilenIzin;
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
        if (izin.getIzinTuru() == null || izin.getIzinTuru().getId() == null) {
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

