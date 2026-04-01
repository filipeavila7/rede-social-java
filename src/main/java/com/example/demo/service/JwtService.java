package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    // Secret key usada para assinar e validar o JWT.
    // Em producao, essa chave deve vir de variavel de ambiente.
    // Lê a chave de uma variável de ambiente
    private final String secret = System.getenv("JWT_SECRET");

    // cria a chave, caso seja null, usa uma padrão para desenvolvimento
    private final SecretKey key = Keys.hmacShaKeyFor(
        (secret != null ? secret : "fallback-dev-secret-com-mais-de-32-caracteres-123")
            .getBytes(StandardCharsets.UTF_8)
    );

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
