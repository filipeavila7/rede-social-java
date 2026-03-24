package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String bio;
    
    @Column
    private String imageUrlProfile;

    @Column
    private String messageStatus;

    @Column
    private LocalDateTime messageStatusCreatedAt;


    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @JsonIgnore
    private User user;


    public Profile() {
    }




    public Profile(String bio, String imageUrlProfile, String messageStatus, User user) {
        this.bio = bio;
        this.imageUrlProfile = imageUrlProfile;
        this.messageStatus = messageStatus;
        this.user = user;
    }


    public Long getId() {
        return id;
    }


    public String getBio() {
        return bio;
    }


    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getImageUrlProfile() {
        return imageUrlProfile;
    }


    public void setImageUrlProfile(String imageUrlProfile) {
        this.imageUrlProfile = imageUrlProfile;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public String getMessageStatus() {
        return messageStatus;
    }


    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public LocalDateTime getMessageStatusCreatedAt() {
        return messageStatusCreatedAt;
    }

    public void setMessageStatusCreatedAt(LocalDateTime messageStatusCreatedAt) {
        this.messageStatusCreatedAt = messageStatusCreatedAt;
    }



    
}
