package com.example.demo.feed.interest.service;

import com.example.demo.feed.interaction.entity.InteractionType;
import com.example.demo.feed.interest.entity.UserInterest;
import com.example.demo.feed.interest.repository.UserInterestRepository;
import com.example.demo.post.entity.Post;
import com.example.demo.tag.entity.Tag;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInterestService {

    private static final double LIKE_WEIGHT = 5.0;
    private static final double COMMENT_WEIGHT = 8.0;
    private static final double SAVE_WEIGHT = 10.0;
    private static final double SHARE_WEIGHT = 12.0;

    private final UserInterestRepository userInterestRepository;

    // usado para VIEW / SAVE / SHARE (interações registradas via UserInteraction)
    @Transactional
    public void registerInterest(User user, Post post, InteractionType type, Integer durationSeconds) {
        double score = calculateScore(type, durationSeconds);
        if (score > 0) {
            applyDelta(user, post, score);
        }
    }

    // usado diretamente por LikeService / CommentService, com delta positivo ou negativo
    @Transactional
    public void applyDelta(User user, Post post, double delta) {
        List<Tag> tags = post.getTags();
        if (tags.isEmpty()) {
            return;
        }

        List<Long> tagIds = tags.stream().map(Tag::getId).toList();

        // busca em lote em vez de uma query por tag
        Map<Long, UserInterest> existing = userInterestRepository
                .findByUserIdAndTagIdIn(user.getId(), tagIds)
                .stream()
                .collect(Collectors.toMap(ui -> ui.getTag().getId(), ui -> ui));

        LocalDateTime now = LocalDateTime.now();

        for (Tag tag : tags) {
            UserInterest interest = existing.getOrDefault(tag.getId(), createInterest(user, tag));

            double newScore = Math.max(0, interest.getScore() + delta);
            interest.setScore(newScore);
            interest.setUpdatedAt(now);

            userInterestRepository.save(interest);
        }
    }

    private UserInterest createInterest(User user, Tag tag) {
        UserInterest interest = new UserInterest();
        interest.setUser(user);
        interest.setTag(tag);
        interest.setScore(0.0);
        interest.setUpdatedAt(LocalDateTime.now());
        return interest;
    }

    private double calculateScore(InteractionType type, Integer durationSeconds) {
        return switch (type) {
            case VIEW -> calculateViewScore(durationSeconds);
            case SAVE -> SAVE_WEIGHT;
            case SHARE -> SHARE_WEIGHT;
        };
    }

    private double calculateViewScore(Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds < 10) return 0;
        if (durationSeconds < 20) return 1;
        if (durationSeconds < 40) return 2;
        if (durationSeconds < 60) return 3;
        if (durationSeconds < 120) return 4;
        return 5;
    }

    // expostos para LikeService/CommentService usarem os mesmos pesos
    public static double likeWeight() { return LIKE_WEIGHT; }
    public static double commentWeight() { return COMMENT_WEIGHT; }
}