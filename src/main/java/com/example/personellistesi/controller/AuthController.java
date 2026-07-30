package com.example.personellistesi.controller;

import com.example.personellistesi.DTO.UserRegistrationDto;
import com.example.personellistesi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        String response = authService.registerUser(dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        String response = authService.activateAccount(token);
        return ResponseEntity.ok(response);
    }

}
