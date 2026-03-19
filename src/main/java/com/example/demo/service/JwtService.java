package com.example.demo.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    // Secret key usada para assinar e validar o JWT.
    // Em producao, essa chave deve vir de variavel de ambiente.
    private final SecretKey key = Keys
            .hmacShaKeyFor("minha-chave-super-secreta-com-mais-de-32-caracteres-123".getBytes());

    public String extrairEmail(String token) {
        // Valida a assinatura e le o "subject" do token (aqui usamos o email).
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String gerarToken(String email) {
        // Cria um token com o email no subject e expira em 1 hora.
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(key)
                .compact();
    }
}
