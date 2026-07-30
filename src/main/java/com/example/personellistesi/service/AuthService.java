package com.example.personellistesi.service;

import com.example.personellistesi.DTO.UserRegistrationDto;
import com.example.personellistesi.model.Kullanıcı;
import com.example.personellistesi.model.Kullanıcı;
import com.example.personellistesi.model.Role;
import com.example.personellistesi.repo.KullanıcıRepository;
import com.example.personellistesi.repo.KullanıcıRepository;
import com.example.personellistesi.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private KullanıcıRepository kullaniciRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JavaMailSender mailSender;

    //  Kayıt Olma Metodu
    public String registerUser(UserRegistrationDto dto) {

        Kullanıcı kullanici = new Kullanıcı();
        kullanici.setUsername(dto.getUsername());
        kullanici.setPassword(dto.getPassword()); // Şimdilik şifreleme yok dediğin için direkt atıyoruz
        kullanici.setEmail(dto.getEmail());
        kullanici.setActive(false); // Başlangıçta pasif (mail onaylayana kadar)

        // Benzersiz Aktivasyon Token'ı üretme
        String token = UUID.randomUUID().toString();
        kullanici.setActivationToken(token);

        // Tek Rol Ataması (ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı"));
        kullanici.setRole(userRole);

        kullaniciRepository.save(kullanici);

        // Aktivasyon Maili Gönderme
        sendActivationEmail(kullanici.getEmail(), token);

        return "Kayıt başarılı! Lütfen hesabınızı aktif etmek için mailinizi kontrol edin.";
    }

    //  Mail Gönderme Yardımcı Metodu
    private void sendActivationEmail(String toEmail, String token) {
        String activationUrl = "http://localhost:8080/api/auth/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Hesap Aktivasyonu - Personel Yönetim Sistemi");
        message.setText("Hesabınızı aktif etmek için aşağıdaki linke tıklayın:\n\n" + activationUrl);

        mailSender.send(message);
    }

    //  Hesabı Aktif Etme Metodu
    public String activateAccount(String token) {
        Kullanıcı kullanici = kullaniciRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz veya süresi dolmuş aktivasyon token'ı!"));

        kullanici.setActive(true);
        kullanici.setActivationToken(null); // Token temizlenir
        kullaniciRepository.save(kullanici);

        return "Hesabınız başarıyla aktive edildi! Giriş yapabilirsiniz.";
    }
}
