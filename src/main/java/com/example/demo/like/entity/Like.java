package com.example.demo.like.entity;

import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.querydsl.QSort;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "likes",
    // evitar duplicação de curtidas
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"})
)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    // relacionamentos

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
