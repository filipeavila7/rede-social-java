package com.example.demo.exeptions.post;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super("Post não encontrado");
    }
}
