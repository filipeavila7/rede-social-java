package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "follows",   
    uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}) // evitar seguidores repetidos
)
public class Follow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // quem segue
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;


    // quem é seguido
    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;


    @Column(nullable = false)
    private LocalDateTime createdAt;


    public Follow() {
    }


    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
        this.createdAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }


    public User getFollower() {
        return follower;
    }


    public void setFollower(User follower) {
        this.follower = follower;
    }


    public User getFollowed() {
        return followed;
    }


    public void setFollowed(User followed) {
        this.followed = followed;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    
    

    
}
