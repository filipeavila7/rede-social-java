package com.example.demo.story.repository;

import com.example.demo.story.entity.Story;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {


    Page<Story> findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
            String userName,
            LocalDateTime now,
            Pageable pageable
    );


    Page<Story> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Optional<Story> findByIdAndExpiresAtAfter(
            Long storyId,
            LocalDateTime now
    );

    Optional<Story> findByIdAndUserId(Long storyId, Long userId);

    List<Story> findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
            String userName,
            LocalDateTime now
    );

    List<Story> findByUserIdAndExpiresAtAfterOrderByCreatedAtAsc(
            Long userId,
            LocalDateTime now
    );


    // boleano se o usuario tem story disponível
    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM Story s
    WHERE s.user.id = :userId
      AND s.expiresAt > CURRENT_TIMESTAMP
""")
    boolean hasActiveStory(@Param("userId") Long userId);


    // verificar se tem story qye não foi vizualizado
    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM Story s
    WHERE s.user.id = :userId
      AND s.expiresAt > CURRENT_TIMESTAMP
      AND NOT EXISTS (
          SELECT sv.id
          FROM StoryVisibilities sv
          WHERE sv.story.id = s.id
            AND sv.user.id = :loggedUserId
      )
""")
    boolean hasUnviewedStory(
            @Param("userId") Long userId,
            @Param("loggedUserId") Long loggedUserId
    );


}
