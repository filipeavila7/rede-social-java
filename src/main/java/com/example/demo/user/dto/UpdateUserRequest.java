package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @Size(min = 2, max = 40, message = "Nome deve ter entre 2 e 40 caracteres")
        String name,


        @Size(min = 3, max = 20, message = "Username deve ter entre 3 e 20 caracteres")
        String userName,


        @Email(message = "Email inválido")
        @Size(max = 100, message = "Email muito longo")
        String email,

        @Size(min = 8, max = 64, message = "Senha deve ter entre 8 e 64 caracteres")
        String password,


) {
}
