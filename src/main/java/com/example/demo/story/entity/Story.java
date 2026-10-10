package com.example.demo.story.entity;

import com.example.demo.likeStory.entity.LikeStory;
import com.example.demo.storyVisibilities.entity.StoryVisibilities;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "story")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String imageUrl;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiresAt;

    @Column
    private String description;

    // cor do fundo do story de texto
    @Column(length = 7)
    private String backgroundColor;


    @Column
    @Enumerated(EnumType.STRING)
    private StoryVisibility visibility;


    @Column
    @Enumerated(EnumType.STRING)
    private StoryType storyType;


    // um ysuario pode postar vários storys
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // um story tem varios likes
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "story")
    private List<LikeStory> likeStories = new ArrayList<>();

    // um story tem varias vizualizações
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "story")
    private List<StoryVisibilities> storyVisibilities = new ArrayList<>();
}