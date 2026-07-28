package com.example.personellistesi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private  JavaMailSender mailSender;

    public  void personelHosgeldinMailiGonder(String aliciMail, String personelAd, String departmanAd, String iseGirisTarihi) {

        // 1. Mail içeriğini doğrudan String olarak oluşturuyoruz ( \n ile alt satıra geçilir )
        String mesajIcerigi = "Merhaba " + personelAd + ",\n\n"
                + "Şirketimize ve Personel Yönetim Sistemimize hoş geldin! Kaydın başarıyla oluşturuldu.\n\n"
                + "Departman: " + (departmanAd != null ? departmanAd : "Belirtilmedi") + "\n"
                + "İşe Giriş Tarihi: " + iseGirisTarihi + "\n\n"
                + "İyi çalışmalar dileriz.";

        // 2. Basit mail nesnesini oluşturma
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fatmanur2328@gmail.com");
        message.setTo(aliciMail);
        message.setSubject("Aramıza Hoş Geldiniz!");
        message.setText(mesajIcerigi);

        // 3. Maili Gönderme
        try {
            mailSender.send(message);
            System.out.println("Mail başarıyla gönderildi: " + aliciMail);
        } catch (Exception e) {
            System.err.println("Mail gönderim hatası: " + e.getMessage());
        }
    }
}
