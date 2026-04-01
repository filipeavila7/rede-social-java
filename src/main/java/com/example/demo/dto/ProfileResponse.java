package com.example.demo.dto;

// dados do perfil do usuario logado
public class ProfileResponse {
    private String nome;
    private String bio;
    private String imageUrlProfile;
    private String messageStatus;

    public ProfileResponse(String nome, String bio, String imageUrlProfile, String messageStatus) {
        this.nome = nome;
        this.bio = bio;
        this.imageUrlProfile = imageUrlProfile;
        this.messageStatus = messageStatus;
    }

    public String getNome() {
        return nome;
    }

    public String getBio() {
        return bio;
    }

    public String getImageUrlProfile() {
        return imageUrlProfile;
    }

    public String getMessageStatus() {
        return messageStatus;
    }
}
