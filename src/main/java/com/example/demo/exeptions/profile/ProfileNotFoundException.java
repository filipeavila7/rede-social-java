package com.example.demo.exeptions.profile;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException() {
        super("Perfil não encontrado");
    }
}
