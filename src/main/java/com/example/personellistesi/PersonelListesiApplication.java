package com.example.personellistesi;

import jakarta.persistence.*;
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
public class PersonelListesiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonelListesiApplication.class, args);
    }

}
