package com.example.demo.follow.entity;

import java.time.LocalDateTime;

import com.example.demo.user.entity.User;
import jakarta.persistence.Column;
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

@Entity
@Table(
    name = "follows",   
    uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}) // evitar seguidores repetidos
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // quem segue
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;


    // quem é seguido
    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;


    @Column(nullable = false)
    private LocalDateTime createdAt;


}
