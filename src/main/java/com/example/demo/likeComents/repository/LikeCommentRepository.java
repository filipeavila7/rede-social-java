package com.example.demo.likeComents.repository;

import com.example.demo.likeComents.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {
}
