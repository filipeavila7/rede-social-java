package com.example.demo.closeFriends.entity;

import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "close-friends",
        uniqueConstraints = {
                @UniqueConstraint(
                        // unicos para não colocar o mesmo amigo duas vezes
                        name = "uk_best_friend_user_friend",
                        columnNames = {"user_id", "friend_id"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseFriends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // usuario que pois no cf

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend; // usuario que foi colocado

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;



}
