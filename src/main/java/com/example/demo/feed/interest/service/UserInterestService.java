package com.example.demo.feed.interest.service;

import com.example.demo.feed.interaction.entity.InteractionType;
import com.example.demo.feed.interest.entity.UserInterest;
import com.example.demo.feed.interest.repository.UserInterestRepository;
import com.example.demo.post.entity.Post;
import com.example.demo.tag.entity.Tag;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserInterestService {
    private final UserInterestRepository userInterestRepository;

    public void registerInterest(
            User user,
            Post post,
            InteractionType type,
            Integer durationSeconds
    ){
        // calcula o score do tipo da interação
        double score = calculateScore(type, durationSeconds);

        for (Tag tag : post.getTags()){
            UserInterest interest =
                    userInterestRepository
                            .findByUserIdAndTagId(user.getId(), tag.getId()) // procura se existe pontuação pra aquela tag
                            .orElseGet(() -> createInterest(user, tag)); // se não existir, cria

            // adciona pontuação
            interest.setScore(
                    interest.getScore() + score
            );
        }

    }

    // criar interesse quando não existir
    private UserInterest createInterest(User user, Tag tag) {

        UserInterest interest = new UserInterest();

        interest.setUser(user);
        interest.setTag(tag);
        interest.setScore(0.0);
        interest.setUpdatedAt(LocalDateTime.now());

        return interest;
    }

    // calulcar pontuação de acordo com o tipo da interação
    private double calculateScore(
            InteractionType type,
            Integer durationSeconds
    ) {

        return switch (type) {

            case VIEW -> calculateViewScore(durationSeconds);

            case LIKE -> 5.0;

            case COMMENT -> 8.0;

            case SAVE -> 10.0;

            case SHARE -> 12.0;
        };
    }

    // metodo exclusivo para calcular o score da vizualização
    private double calculateViewScore(Integer durationSeconds) {

        if (durationSeconds == null || durationSeconds < 10) {
            return 0;
        }

        if (durationSeconds < 20) {
            return 1;
        }

        if (durationSeconds < 40) {
            return 2;
        }

        if (durationSeconds < 60) {
            return 3;
        }

        if (durationSeconds < 120) {
            return 4;
        }

        return 5;
    }
}
