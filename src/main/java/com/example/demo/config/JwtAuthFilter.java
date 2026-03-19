package com.example.demo.config;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // Fluxo geral:
    // 1) Le o header Authorization.
    // 2) Extrai o email do token.
    // 3) Coloca autenticacao no contexto do Spring.
    //OncePerRequestFilter é um filtro do Spring que executa apenas uma vez por requisição.
    // Esse filtro vai interceptar todas as requisições HTTP antes de chegar aos controllers.
    // Objetivo: verificar se existe um JWT válido e colocar a autenticação no contexto do Spring Security.

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        // se não tem token, segue sem autenticar
        if (auth == null || !auth.toLowerCase().startsWith("bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7).trim();

        // extrair email do token
        String email;
        try {
            email = jwtService.extrairEmail(token);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // aqui autenticamos o usuário no contexto
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
