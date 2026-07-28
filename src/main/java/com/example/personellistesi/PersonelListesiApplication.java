package com.example.personellistesi;

import com.example.personellistesi.service.MailService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.personellistesi.repo")
@EntityScan(basePackages = "com.example.personellistesi.*")
public class PersonelListesiApplication implements CommandLineRunner {

    @Autowired
    private MailService mailService;

    public static void main(String[] args) {

        SpringApplication.run(PersonelListesiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Mail testi tetikleniyor...");

        // Alıcı kısmına geçici olarak kendi e-posta adresini yazarak test edebilirsin
        mailService.personelHosgeldinMailiGonder(
                "fatmanuryrdl@gmail.com",
                "Fatma",
                "Yazılım",
                "2026-07-28"
        );
    }


}
