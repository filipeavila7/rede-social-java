package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity // definir que a classe será uma entity, representará a tabela no banco
@Table(name = "users") // definir que é uma tabela e passar o nom
public class User {

    @Id // delcarar que sera um id no banco
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincremente 1,2,3,4...
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;


    public String getSenha() {
        return senha;
    }


    public void setSenha(String senha) {
        this.senha = senha;
    }


    public User(){

    }

    
    public User(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }


    // umm usuario tem varios posts
    // mappedBy = "user" → significa que o controle da relação está na classe Post
    // (lá existe o atributo "user")
    // signfica que a chave estrangeira ficara no post, curtida e like
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Post> posts;


    // um usuario tem várias curtidas
    @OneToMany(mappedBy =  "user")
    @JsonIgnore
    private List<Like> likes;


    // um usuario tem varios comentarios
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<CommentUser> comments;





    public Long getId() {
        return id;
    }


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public List<Post> getPosts() {
        return posts;
    }


    public List<Like> getLikes() {
        return likes;
    }


    public List<CommentUser> getComments() {
        return comments;
    }

    

    


}
