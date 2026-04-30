package com.example.demo.dto;

// dados do perfil dos usuarios seguidos
public class FollowingProfileResponse {
    private Long userId;
    private String nome;
    private String imageUrlProfile;
    private String messageStatus;
    private String UserName;


    public FollowingProfileResponse(Long userId, String nome, String imageUrlProfile, String messageStatus, String userName) {
        this.userId = userId;
        this.nome = nome;
        this.imageUrlProfile = imageUrlProfile;
        this.messageStatus = messageStatus;
        UserName = userName;
    }

    public Long getUserId() { return userId; }

    public String getUserName() {
        return UserName;
    }

    public String getNome() { return nome; }
    public String getImageUrlProfile() { return imageUrlProfile; }
    public String getMessageStatus() { return messageStatus;}
}
