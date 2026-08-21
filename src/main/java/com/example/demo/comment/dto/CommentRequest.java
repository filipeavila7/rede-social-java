package com.example.demo.comment.dto;

import jakarta.validation.constraints.Size;

public record CommentRequest(
        @Size(max = 500, message = "No máximo 500 caracteres")
        String content
) {
}
