package com.example.personellistesi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI personelYonetimOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personel Yönetim Sistemi API")
                        .description("Personel, Departman, İzin ve Bildirim loglarının yönetildiği Spring Boot REST API dökümantasyonu.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Fatmanur Yurdakul")
                                .email("fatmanuryrdl@gmail.com")));
    }
}
