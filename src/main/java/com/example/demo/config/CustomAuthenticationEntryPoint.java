package com.example.demo.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


// tratar as exceptions do spring security
//token ausente
//token inválido
//sessão expirada

@Component
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {


    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {


        response.setStatus(HttpStatus.NOT_FOUND.value());

        response.setContentType("application/json");

        response.getWriter().write("""
            {
              "erro": "Não encontrado"
            }
        """);
    }
}