package com.example.demo.user.entity;

import java.util.List;

import com.example.demo.comment.entity.Comment;
import com.example.demo.follow.entity.Follow;
import com.example.demo.like.entity.Like;
import com.example.demo.entity.Profile;
import com.example.demo.post.entity.Post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/* 
pontos importantes na hora da associação:

1 - um usuario tem varios posts, ou seja, oneToMany, o usuario tera uma lista de posts como associação
ele terá o mapped by, pq a fk key dele ficara em Post

2 - váriios posts pertecem A UM SÓ usuario, a fk key, metodo set ficará no post, pois ele tem o controle da relação por ter a fk de user

3 - post tem que ter um setUser, pois ele controla a relação, e ele pode passaar apenas umm usuario, caso fosse o user que passase o set posts, ele passaria uma lista de post que não são dele

4 - o mapped by ->  diz que essa lista posts está ligada ao campo "User user" que existe dentro de Post



*/

@Entity // definir que a classe será uma entity, representará a tabela no banco
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    // um usuário tem vários posts
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;


    // um usuario tem várias curtidas
    @OneToMany(mappedBy =  "user", cascade = CascadeType.ALL)
    private List<Like> likes;


    // um usuario tem varios comentarios
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;


    // um usuario so pode ter um perfil
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;


    // um usuario pode ter varios seguidores
    @OneToMany(mappedBy = "followed")
    private List<Follow> followers;

    // um usuario pode seguir varios usuarios
    @OneToMany(mappedBy =  "follower")
    private List<Follow> following;


}
