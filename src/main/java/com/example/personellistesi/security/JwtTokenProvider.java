package com.example.personellistesi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // Token imzalamak için gizli anahtar (Gerçek projelerde application.properties'e yazılır)
    private  Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private long jwtExpirationInMs = 86400000;   //Hesap 1 gün kayıtlı olacak


    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // Kullanıcının rolünü token içine gömüyoruz
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    //  Token'dan Kullanıcı Adını Çözme
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //  Token Geçerlilik Kontrolü
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Süresi dolmuş, bozuk veya geçersiz token
            return false;
        }
    }
}
