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
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;


    public String registerUser(UserRegistrationDto dto) {

        Kullanıcı kullanici = new Kullanıcı();
        kullanici.setUsername(dto.getUsername());
        kullanici.setPassword(passwordEncoder.encode(dto.getPassword()));
        kullanici.setEmail(dto.getEmail());
        kullanici.setActive(false);


        String token = UUID.randomUUID().toString();
        kullanici.setActivationToken(token);


        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı"));
        kullanici.setRole(userRole);

        kullaniciRepository.save(kullanici);


        mailService.personelHosgeldinMailiGonder(kullanici.getEmail(), kullanici.getUsername(), token);


        return "Kayıt başarılı! Lütfen hesabınızı aktif etmek için mailinizi kontrol edin.";
    }


    public String activateAccount(String token) {
        Kullanıcı kullanici = kullaniciRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz veya süresi dolmuş aktivasyon token'ı!"));

        kullanici.setActive(true);
        kullanici.setActivationToken(null);
        kullaniciRepository.save(kullanici);

        return "Hesabınız başarıyla aktive edildi! Giriş yapabilirsiniz.";
    }


    public LoginResponseDto loginUser(LoginRequestDto dto) {
        Kullanıcı kullanici = kullaniciRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı adı veya şifre hatalı!"));

        if(!passwordEncoder.matches(dto.getPassword(), kullanici.getPassword())) {
            throw new RuntimeException("Kullanıcı adı veya şifre hatalı!");
        }

        if (!kullanici.isActive()) {
            throw new RuntimeException("Hesabınız aktif değil! Lütfen önce mailinizdeki aktivasyon linkine tıklayın.");
        }

        String token = tokenProvider.generateToken(kullanici.getUsername(), kullanici.getRole().getName());

        return new LoginResponseDto(token, "Giriş başarılı!");
    }

}
