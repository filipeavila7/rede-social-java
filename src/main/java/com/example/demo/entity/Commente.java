package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Commente {

    @Id // delcarar que sera um id no banco
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Post post;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;


    public Commente() {
    }


    public Commente(String content, Post post, User user) {
        this.content = content;
        this.post = post;
        this.user = user;
    }


    public Long getId() {
        return id;
    }



    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public Post getPost() {
        return post;
    }


    public void setPost(Post post) {
        this.post = post;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {return createdAt;}


    


    


    
}
