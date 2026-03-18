package com.example.demo.entity;

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
    name = "likes",
    // evitar duplicação de curtidas
    uniqueConstraints = @UniqueConstraint(columnNames = {"users_id", "post_id"})
)
public class Like {
    @Id // delcarar que sera um id no banco
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;


    // fk de post, Muitos Likes podem se relacionar com um mesmo Post, mas cada Like pertence a exatamente um Post.
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // fk de user, muitos likes pertecem a um user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Like() {
    }

    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public Long getId() {
        return id;
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
