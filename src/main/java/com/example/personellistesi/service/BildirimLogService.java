package com.example.personellistesi.service;

import com.example.personellistesi.model.BildirimLog;
import com.example.personellistesi.repo.BildirimLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BildirimLogService implements IBildirimLogService {

    private BildirimLogRepository bildirimLogRepository;
    private JavaMailSender mailSender;

    @Autowired
    public BildirimLogService(BildirimLogRepository bildirimLogRepository, JavaMailSender mailSender) {
        this.bildirimLogRepository = bildirimLogRepository;
        this.mailSender = mailSender;
    }

    @Override
    public BildirimLog saveLog(BildirimLog bildirimLog) {
        gercekMailiGonder(bildirimLog.getToAddress(), bildirimLog.getSubject(), bildirimLog.getContent());
        return bildirimLogRepository.save(bildirimLog);
    }

    private void gercekMailiGonder(String kime, String konu, String icerik) {
        try {
            SimpleMailMessage mesaj = new SimpleMailMessage();
            mesaj.setFrom("senin.mailin@gmail.com"); // application.properties'e yazdığın mail adresini buraya da yaz
            mesaj.setTo(kime);
            mesaj.setSubject(konu);
            mesaj.setText(icerik);

            mailSender.send(mesaj);
            System.out.println("E-posta başarıyla gönderildi: " + kime);
        } catch (Exception e) {
            System.err.println("E-posta GÖNDERİLEMEDİ! Hata: " + e.getMessage());
            // Mail gönderilemese bile hata fırlatmıyoruz ki veritabanı loglama işlemi yarıda kesilmesin.
        }
    }


    @Override
    public BildirimLog getLogById(String id) {
        return bildirimLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hata: " + id + " numaralı log bulunamadı!"));
    }

    @Override
    public List<BildirimLog> getAllLogs() {
        return bildirimLogRepository.findAll();
    }

    @Override
    public List<BildirimLog> getLogsByToAddress(String toAddress) {
        return bildirimLogRepository.findByToAddress(toAddress);
    }
}
