package com.example.demo.resetpassword.dto;

public record ResetPasswordRequest(
        String token,
        String novaSenha
) {
}
