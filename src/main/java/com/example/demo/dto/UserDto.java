package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(

        @NotBlank(message = "Nome obrigatório")
        @Size(min = 2, max = 40, message = "Nome deve ter entre 2 e 40 caracteres")
        String nome,

        @NotBlank(message = "Username obrigatório")
        @Size(min = 3, max = 20, message = "Username deve ter entre 3 e 20 caracteres")
        String userName,

        @NotBlank(message = "Email obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 100, message = "Email muito longo")
        String email,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 8, max = 64, message = "Senha deve ter entre 8 e 64 caracteres")
        String senha
){}