package com.example.personellistesi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void personelHosgeldinMailiGonder(String aliciMail, String personelAd, String departmanAd, String iseGirisTarihi) {
        try {
            // 1. Thymeleaf Context (Değişkenleri hazırlama)
            Context context = new Context();
            context.setVariable("personelAd", personelAd);
            context.setVariable("departmanAd", departmanAd);
            context.setVariable("iseGiris", iseGirisTarihi);

            // 2. HTML Şablonunu işleme
            String htmlIcerik = templateEngine.process("hosgeldin-mail", context);

            // 3. Mail nesnesini oluşturma
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(aliciMail);
            helper.setSubject("Aramıza Hoş Geldiniz!");
            helper.setText(htmlIcerik, true); // true parametresi mailin HTML olduğunu belirtir

            // 4. Maili Gönderme
            mailSender.send(message);
            System.out.println("Mail başarıyla gönderildi: " + aliciMail);

        } catch (MessagingException e) {
            System.err.println("Mail gönderim hatası: " + e.getMessage());
        }
    }
}
