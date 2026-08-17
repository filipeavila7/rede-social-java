package com.example.demo.feed.repository;

import com.example.demo.feed.entity.PostImpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;

public interface PostImpressionRepository extends JpaRepository<PostImpression, Long> {

    @Query("""
        SELECT DISTINCT pi.post.id FROM PostImpression pi
        WHERE pi.user.id = :userId AND pi.shownAt >= :since
        """)
    Set<Long> findRecentlyShownPostIds(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    void deleteByShownAtBefore(LocalDateTime cutoff); // limpeza periódica, ver observação no fim
}