package com.example.demo.repository;

import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);
    PasswordResetToken findByUser(User user);

    Optional<PasswordResetToken>
    findByUserAndExpiracaoAfter(
            User user,
            LocalDateTime agora
    );
}