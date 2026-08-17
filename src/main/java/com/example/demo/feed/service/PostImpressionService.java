package com.example.demo.feed.service;

import com.example.demo.feed.entity.PostImpression;
import com.example.demo.feed.repository.PostImpressionRepository;
import com.example.demo.post.entity.Post;
import com.example.demo.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImpressionService {

    private static final int RETENTION_DAYS = 2;

    private final PostImpressionRepository postImpressionRepository;

    @Async
    @Transactional
    public void registerImpressions(User user, List<Post> shownPosts) {
        if (shownPosts.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        List<PostImpression> impressions = shownPosts.stream()
                .map(post -> {
                    PostImpression impression = new PostImpression();
                    impression.setUser(user);
                    impression.setPost(post); // entidade já em mãos, sem query extra
                    impression.setShownAt(now);
                    return impression;
                })
                .toList();

        postImpressionRepository.saveAll(impressions);
    }

    // chamado por um @Scheduled em algum lugar da aplicação (ver observação abaixo)
    @Transactional
    public void purgeOldImpressions() {
        postImpressionRepository.deleteByShownAtBefore(LocalDateTime.now().minusDays(RETENTION_DAYS));
    }
}