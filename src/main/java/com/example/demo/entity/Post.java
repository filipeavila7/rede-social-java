package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.util.FileUrlUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String content;

    @Column(nullable = false)
    @NotBlank
    private String imageUrl;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 50)
    private String description;


    @Column
    private LocalDateTime createdAt;


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
    private List<Like> likes; // lista de likes, jpa ja retorna todos os registros de likes que estão associados a fk de post, ou seja todos os likes que tem o id desse post


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
        this.createdAt = LocalDateTime.now();
    }




    // mostrar no json o total de curtidas e comentáario contando o tamanho da lista
    @JsonProperty("likesCount")
    public int getLikesCount() {
        return likes == null ? 0 : likes.size();
    }

    @JsonProperty("commentsCount")
    public int getCommentsCount() {
        return comments == null ? 0 : comments.size();
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
        return FileUrlUtils.toPublicUrl(imageUrl);
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = FileUrlUtils.normalizeStoredPath(imageUrl);
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public List<Like> getLikes() {
        return likes;
    }



    public List<Commente> getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Post [id=" + id + ", content=" + content + ", imageUrl=" + imageUrl + ", createdAt=" + createdAt + "]";
    }


    



    


    
    



}
