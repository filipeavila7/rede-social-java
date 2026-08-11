package com.example.demo.auth.service;

import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.LoginResponse;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.service.JwtService;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AuthService {

    private final GlobalHelperService globalHelperService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        User user = globalHelperService.findUserByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String accessToken = jwtService.gerarToken(user);

        return new LoginResponse(accessToken);
    }

}
