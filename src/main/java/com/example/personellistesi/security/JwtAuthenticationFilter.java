package com.example.personellistesi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. İstekten Header'ı al
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Header "Bearer " ile başlıyor mu kontrol et
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7); // "Bearer " kısmını at, sadece token'ı al
            try {
                username = tokenProvider.getUsernameFromToken(token);
            } catch (Exception e) {
                System.err.println("Token okunamadı: " + e.getMessage());
            }
        }

        // 3. Kullanıcı adı bulunduysa ve sistemde henüz oturum açılmadıysa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (tokenProvider.validateToken(token)) {
                // Kullanıcıyı doğrulanmış olarak Spring Security'ye kaydet
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Yoluna devam et
        filterChain.doFilter(request, response);
    }

}


