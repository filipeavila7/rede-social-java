package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String imageUrl;


    // muitos post pra um usuario
    // aqui fica a fk do usuario no banco, user_id
    // quem manda é a relação
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    // UM POST TEM VÁRIAS CURTIDAS
    // mappedBy = "post" → o atributo "post" está na classe Like, ou seja, a fk
    // NÃO cria coluna no banco
    // o JPA busca na tabela Like usando post_id
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnore // evita loop infinito na API
    private List<Like> likes;


    // UM POST TEM VÁRIOS COMENTÁRIOS
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Commente> comments;


    public Post() {
    }


    public Post(String content, String imageUrl, User user) {
        this.content = content;
        this.imageUrl = imageUrl;
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


    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public List<Like> getLikes() {
        return likes;
    }



    public List<Commente> getComments() {
        return comments;
    }


    @Override
    public String toString() {
        return "Post [id=" + id + ", content=" + content + ", imageUrl=" + imageUrl + "]";
    }


    



    


    
    



}
