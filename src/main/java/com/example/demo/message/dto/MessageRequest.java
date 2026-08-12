package com.example.demo.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotBlank
        @Size(max = 1000)
        String textMessage
) {
}
