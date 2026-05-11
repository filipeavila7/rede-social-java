package com.example.demo.dto;

public record ResetPasswordRequest(
        String token,
        String novaSenha
) {
}
