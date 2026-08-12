package com.example.demo.resetpassword.controller;

import com.example.demo.resetpassword.dto.ForgotPasswordRequest;
import com.example.demo.resetpassword.dto.ResetPasswordRequest;
import com.example.demo.resetpassword.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reset")
@RequiredArgsConstructor
public class PasswordResetTokenController {
    private final PasswordResetTokenService service;

    //  TODO - deixar que o frontEnd envie o link ou a url do dominio para montar o link

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {
        service.solicitarReset(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-token")
    public ResponseEntity<Void> resendToken(
            @RequestBody ForgotPasswordRequest request
    ) {
        service.reenviarEmail(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        service.resetPassword(request.token(), request.novaSenha());
        return ResponseEntity.ok().build();
    }
}
