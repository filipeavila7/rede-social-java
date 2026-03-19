package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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


    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;


    public Profile() {
    }


    public Profile(String bio, String imageUrlProfile, User user) {
        this.bio = bio;
        this.imageUrlProfile = imageUrlProfile;
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


    
}
