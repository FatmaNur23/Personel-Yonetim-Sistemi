package com.example.personellistesi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private  JavaMailSender mailSender;

    public void personelHosgeldinMailiGonder(String aliciMail, String personelAd,String token) {

        String aktivasyonUrl = "http://localhost:8080/api/auth/activate?token=" + token;

        String mesajIcerigi = "Merhaba " + personelAd + ",\n\n"
                + "Şirketimize ve Personel Yönetim Sistemimize hoş geldin! Kaydının tamamlanması için lütfen aşağıdaki linke tıkla:\n\n"
                + aktivasyonUrl + "\n\n"
                + "İyi çalışmalar dileriz.";


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fatmanur2328@gmail.com");
        message.setTo(aliciMail);
        message.setSubject("Aramıza Hoş Geldiniz!");
        message.setText(mesajIcerigi);


        try {
            mailSender.send(message);
            System.out.println("Mail başarıyla gönderildi: " + aliciMail);
        } catch (Exception e) {
            System.err.println("Mail gönderim hatası: " + e.getMessage());
        }
    }
}
