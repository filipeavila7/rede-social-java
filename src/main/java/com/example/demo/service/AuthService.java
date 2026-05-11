package com.example.demo.service;

import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 1 - pedir reset de senha
    public void solicitarReset(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // gerar um token aleatorio
        String token = UUID.randomUUID().toString();

        // adcionar token, usuario e data de expiração na entity de token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiracao(LocalDateTime.now().plusMinutes(30));

        // salvar no banco
        tokenRepository.save(resetToken);

        // link de recuperação
        String link = "http://localhost:5173/reset-password?token=" + token;

        // enviar email
        emailService.enviarEmailReset(user.getEmail(), link);
    }

    // 2 - redefinir senha
    public void redefinirSenha(String token, String novaSenha) {
        // procurar pelo token, para confirmar se o usuario pertence a ele
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // verifica se a data de expiração é antes da data atual
        if (resetToken.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        // pegar user dono do token
        User user = resetToken.getUser();

        // nova senha
        user.setSenha(encoder.encode(novaSenha));

        // salvar a nova senha
        userRepository.save(user);

        tokenRepository.delete(resetToken); // invalida token
    }
}