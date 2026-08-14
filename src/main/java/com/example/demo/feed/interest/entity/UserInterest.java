package com.example.demo.feed.interest.entity;

// Representa o perfil de interesse atual do usuário
// com base nas tags dos posts

import com.example.demo.tag.entity.Tag;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_interest",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "tag_id"})
        })

public class UserInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;


    @Column
    private double score;

    @Column
    private LocalDateTime updatedAt;
}
