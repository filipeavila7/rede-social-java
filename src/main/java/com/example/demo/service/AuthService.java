package com.example.demo.service;

import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {



    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // =========================
    // 1 - SOLICITAR RESET SENHA
    // =========================
    public void solicitarReset(String email) {

        // busca usuário pelo email
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // tenta buscar token já existente para esse usuário
        PasswordResetToken existingToken =
                tokenRepository.findByUser(user);

        // =========================
        // COOLDOWN DE 1 MINUTO
        // =========================
        // evita spam de email em curto tempo
        if (existingToken != null
                && existingToken.getUltimoEnvio() != null
                && existingToken.getUltimoEnvio()
                .plusMinutes(1)
                .isAfter(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Aguarde 1 minuto antes de solicitar outro email"
            );
        }

        // gera novo token seguro
        String tokenValue = UUID.randomUUID().toString();

        PasswordResetToken resetToken;

        // =========================
        // REUTILIZA OU CRIA TOKEN
        // =========================
        if (existingToken != null) {
            // reutiliza registro existente (evita duplicação no banco)
            resetToken = existingToken;
        } else {
            // cria novo registro
            resetToken = new PasswordResetToken();
            resetToken.setUser(user);
        }

        // atualiza dados do token
        resetToken.setToken(tokenValue);
        resetToken.setUltimoEnvio(LocalDateTime.now());
        resetToken.setExpiracao(LocalDateTime.now().plusMinutes(15));

        // salva no banco (update ou insert)
        tokenRepository.save(resetToken);

        // monta link de reset
        String link = "http://localhost:5173/reset-password?token=" + tokenValue;

        // envia email
        emailService.enviarEmailReset(user.getEmail(), link);
    }

    // =========================
    // 2 - REDEFINIR SENHA
    // =========================
    public void redefinirSenha(String token, String novaSenha) {

        // busca token no banco
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // verifica se expirou
        if (resetToken.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        // pega usuário dono do token
        User user = resetToken.getUser();

        // criptografa nova senha
        user.setSenha(encoder.encode(novaSenha));

        // salva nova senha
        userRepository.save(user);

        // invalida token após uso
        tokenRepository.delete(resetToken);
    }

    // =========================
    // 3 - REENVIAR EMAIL (OPCIONAL)
    // =========================
    public void reenviarEmail(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // busca token válido (não expirado)
        Optional<PasswordResetToken> tokenOpt =
                tokenRepository.findByUserAndExpiracaoAfter(
                        user,
                        LocalDateTime.now()
                );

        PasswordResetToken token;

        if (tokenOpt.isPresent()) {

            token = tokenOpt.get();

            // cooldown de 1 minuto para reenvio
            if (token.getUltimoEnvio() != null
                    && token.getUltimoEnvio()
                    .plusMinutes(1)
                    .isAfter(LocalDateTime.now())) {

                throw new RuntimeException(
                        "Espere 1 minuto antes de reenviar outro email"
                );
            }

        } else {
            // cria novo token se não existir válido
            token = new PasswordResetToken();
            token.setUser(user);
            token.setToken(UUID.randomUUID().toString());
            token.setExpiracao(LocalDateTime.now().plusMinutes(15));
        }

        // atualiza envio
        token.setUltimoEnvio(LocalDateTime.now());

        // salva alterações
        tokenRepository.save(token);

        // envia email
        String link =
                "http://localhost:5173/reset-password?token=" +
                        token.getToken();

        emailService.enviarEmailReset(user.getEmail(), link);
    }
}