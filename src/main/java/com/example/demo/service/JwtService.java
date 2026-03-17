package com.example.demo.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    // chave secreta para assinar o token
    private final SecretKey key = Keys.hmacShaKeyFor("minha-chave-super-secreta-com-mais-de-32-caracteres-123".getBytes());



    public String gerarToken(String email){
        return Jwts.builder() // criar token
        .subject(email) // assunto
        .issuedAt(new Date()) // data de criação
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h, duração do token
        .signWith(key) // o token é assinado com sua chave secreta, para segurança e conseguir validar depois
        .compact(); // compactar tudo para string
    }
}
