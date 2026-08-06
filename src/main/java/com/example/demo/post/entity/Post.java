package com.example.demo.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Commente;
import com.example.demo.entity.Like;
import com.example.demo.entity.Tag;
import com.example.demo.user.entity.User;
import com.example.demo.util.FileUrlUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String description;


    @Column
    private LocalDateTime createdAt;



    // relacionamentos

    // um post tem um user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // um post tem varios likes
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Like> likes;

    // um post tem varios comentarios
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Commente> comments;


    // tabela intermediária que relaciona post com tag (post_tags) -> post_id | tag_id
    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    // TODO dar uma olhada nisso depois

    // mostrar no json o total de curtidas e comentáario contando o tamanho da lista
    @JsonProperty("likesCount")
    public int getLikesCount() {
        return likes == null ? 0 : likes.size();
    }

    @JsonProperty("commentsCount")
    public int getCommentsCount() {
        return comments == null ? 0 : comments.size();
    }



}
