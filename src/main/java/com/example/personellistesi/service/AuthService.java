package com.example.personellistesi.service;

import com.example.personellistesi.DTO.LoginRequestDto;
import com.example.personellistesi.DTO.LoginResponseDto;
import com.example.personellistesi.DTO.UserRegistrationDto;
import com.example.personellistesi.model.Kullanıcı;
import com.example.personellistesi.model.Kullanıcı;
import com.example.personellistesi.model.Role;
import com.example.personellistesi.repo.KullanıcıRepository;
import com.example.personellistesi.repo.KullanıcıRepository;
import com.example.personellistesi.repo.RoleRepository;
import com.example.personellistesi.security.JwtTokenProvider;
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

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private MailService mailService;

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
        mailService.personelHosgeldinMailiGonder(kullanici.getEmail(), kullanici.getUsername(), token);


        return "Kayıt başarılı! Lütfen hesabınızı aktif etmek için mailinizi kontrol edin.";
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


    public LoginResponseDto loginUser(LoginRequestDto dto) {
        // 1. Kullanıcıyı bul
        Kullanıcı kullanici = kullaniciRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre hatalı!"));

        // 2. Şifre kontrolü (Şimdilik düz metin karşılaştırıyoruz, ileride PasswordEncoder ekleyebiliriz)
        if (!kullanici.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı!");
        }

        // 3. Hesap aktif mi kontrolü (Aktivasyon mailine tıklamış mı?)
        if (!kullanici.isActive()) {
            throw new RuntimeException("Hesabınız aktif değil! Lütfen önce mailinizdeki aktivasyon linkine tıklayın.");
        }

        // 4. Her şey yolundaysa JWT Token üret
        String token = tokenProvider.generateToken(kullanici.getUsername(), kullanici.getRole().getName());

        return new LoginResponseDto(token, "Giriş başarılı!");
    }

}
