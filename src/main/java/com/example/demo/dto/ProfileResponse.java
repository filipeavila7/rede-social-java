package com.example.demo.dto;

// dados do perfil do usuario logado
public class ProfileResponse {
    private String nome;
    private String bio;
    private String imageUrlProfile;
    private String messageStatus;
    private String userName;

    public ProfileResponse(String nome, String bio, String imageUrlProfile, String messageStatus, String userName) {
        this.nome = nome;
        this.bio = bio;
        this.imageUrlProfile = imageUrlProfile;
        this.messageStatus = messageStatus;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }
}
