package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class CommentUser {

    @Id // delcarar que sera um id no banco
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = false)
    private String content;


    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    Post post;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;


    public CommentUser() {
    }


    public CommentUser(String content, Post post, User user) {
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


    


    


    
}
