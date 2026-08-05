package com.example.personellistesi.service;

import com.example.personellistesi.model.BildirimLog;
import com.example.personellistesi.repo.BildirimLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
        log.info("Bildirim kaydı işlemi başlatılıyor. Alıcı: {}, Konu: {}",
                bildirimLog.getToAddress(), bildirimLog.getSubject());

        gercekMailiGonder(bildirimLog.getToAddress(), bildirimLog.getSubject(), bildirimLog.getContent());

        BildirimLog kaydedilenLog = bildirimLogRepository.save(bildirimLog);
        log.info("Bildirim logu veritabanına başarıyla kaydedildi. Log ID: {}", kaydedilenLog.getId());
        return kaydedilenLog;
    }

    private void gercekMailiGonder(String kime, String konu, String icerik) {
        try {
            log.debug("Mail gönderme isteği hazırlanıyor: Target -> {}", kime);
            SimpleMailMessage mesaj = new SimpleMailMessage();
            mesaj.setFrom("senin.mailin@gmail.com");
            mesaj.setTo(kime);
            mesaj.setSubject(konu);
            mesaj.setText(icerik);

            mailSender.send(mesaj);
            log.info("E-posta başarıyla gönderildi -> Alıcı: {}", kime);

        } catch (Exception e) {
            log.error("E-posta gönderimi sırasında HATA oluştu! Alıcı: {}, Hata Mesajı: {}", kime, e.getMessage(), e);
        }
    }


    @Override
    public BildirimLog getLogById(String id) {
        log.debug("Log aranıyor, ID: {}", id);
        return bildirimLogRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Aranan log veritabanında bulunamadı! ID: {}", id);
                    return new IllegalArgumentException("Hata: " + id + " numaralı log bulunamadı!");
                });
    }

    @Override
    public List<BildirimLog> getAllLogs() {
        log.info("Tüm bildirim logları listeleniyor.");
        return bildirimLogRepository.findAll();
    }

    @Override
    public List<BildirimLog> getLogsByToAddress(String toAddress) {
        log.info("E-posta adresine göre loglar getiriliyor: {}", toAddress);
        return bildirimLogRepository.findByToAddress(toAddress);
    }
}
