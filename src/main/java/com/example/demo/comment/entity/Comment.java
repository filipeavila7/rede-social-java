package com.example.demo.comment.entity;

import com.example.demo.likeComents.entity.LikeComment;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "comments")
public class Comment {

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


    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<LikeComment> likeComments;



    


    


    
}
