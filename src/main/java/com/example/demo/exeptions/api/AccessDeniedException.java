package com.example.demo.exeptions.api;

public class AccessDeniedException extends RuntimeException{

    public AccessDeniedException() {
        super("Acesso negado");
    }
}
