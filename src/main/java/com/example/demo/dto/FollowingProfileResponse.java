package com.example.demo.dto;

// dados do perfil dos usuarios seguidos 
public class FollowingProfileResponse {
    private Long userId;
    private String nome;
    private String imageUrlProfile;
    private String messageStatus;
    

    public FollowingProfileResponse(Long userId, String nome, String imageUrlProfile, String messageStatus) {
        this.userId = userId;
        this.nome = nome;
        this.imageUrlProfile = imageUrlProfile;
        this.messageStatus = messageStatus;
    }

    public Long getUserId() { return userId; }
    public String getNome() { return nome; }
    public String getImageUrlProfile() { return imageUrlProfile; }
    public String getMessageStatus() { return messageStatus;}
}
