package com.example.demo.likeComents.repository;

import com.example.demo.likeComents.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {
    long countByCommentId(Long commentId);
    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);
}
