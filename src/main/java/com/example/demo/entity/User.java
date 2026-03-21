package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;


/* 
pontos importantes na hora da associação:

1 - um usuario tem varios posts, ou seja, oneToMany, o usuario tera uma lista de posts como associação
ele terá o mapped by, pq a fk key dele ficara em Post

2 - váriios posts pertecem A UM SÓ usuario, a fk key, metodo set ficará no post, pois ele tem o controle da relação por ter a fk de user

3 - post tem que ter um setUser, pois ele controla a relação, e ele pode passaar apenas umm usuario, caso fosse o user que passase o set posts, ele passaria uma lista de post que não são dele

4 - o mapped by ->  diz que essa lista posts está ligada ao campo "User user" que existe dentro de Post



*/

@Entity // definir que a classe será uma entity, representará a tabela no banco
@Table(name = "users") // definir que é uma tabela e passar o nom
public class User {

    @Id // delcarar que sera um id no banco
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincremente 1,2,3,4...
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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


    
    public User(String nome, String email, String senha, Profile profile) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.profile = profile;
    }





    // umm usuario tem varios posts
    // mappedBy = "user" → significa que o controle da relação está na classe Post
    // (lá existe o atributo "user")
    // signfica que a chave estrangeira ficara no post, curtida e like
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> posts;


    // um usuario tem várias curtidas
    @OneToMany(mappedBy =  "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Like> likes;


    // um usuario tem varios comentarios
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Commente> comments;


    // um usuario so pode ter um perfil
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Profile profile;
    

    // um usuario pode ter varios seguidores
    @OneToMany(mappedBy = "followed")
    private List<Follow> followers;

    // um usuario pode seguir varios usuarios
    @OneToMany(mappedBy =  "follower")
    private List<Follow> following;
    
    
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


    public List<Commente> getComments() {
        return comments;
    }


    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) { 
    this.profile = profile;
    }


    @Override
    public String toString() {
        return "User [id=" + id + ", nome=" + nome + ", email=" + email + ", senha=" + senha + ", posts=" + posts
                + ", likes=" + likes + ", comments=" + comments + ", profile=" + profile + "]";
    }


    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }


    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }


    public void setComments(List<Commente> comments) {
        this.comments = comments;
    }

    


    
    

}
