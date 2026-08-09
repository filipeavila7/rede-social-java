package com.example.demo.exeptions.like;

public class LikeNotFoundException extends RuntimeException{
    public LikeNotFoundException() {
        super("Like não encontrado");
    }
}
