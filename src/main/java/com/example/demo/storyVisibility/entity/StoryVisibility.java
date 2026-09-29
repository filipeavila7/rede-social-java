package com.example.demo.storyVisibility.entity;

import com.example.demo.story.entity.Story;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "story-visibility", uniqueConstraints = {
        @UniqueConstraint(
                // não permitir visualizar mais de uma vez
                columnNames = {"user_id", "story_id"}
        )
})
public class StoryVisibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
