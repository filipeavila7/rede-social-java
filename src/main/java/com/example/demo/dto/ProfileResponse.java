package com.example.demo.dto;

// dados do perfil do usuario logado
public class ProfileResponse {
    private String nome;
    private String bio;
    private String imageUrlProfile;
    private String messageStatus;
    private String userName;
    private Long id;


    public ProfileResponse(String nome, String bio, String imageUrlProfile, String messageStatus, String userName, Long id) {
        this.nome = nome;
        this.bio = bio;
        this.imageUrlProfile = imageUrlProfile;
        this.messageStatus = messageStatus;
        this.userName = userName;
        this.id = id;
    }

    public Long getId() {
        return id;
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
