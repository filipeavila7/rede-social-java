package com.example.demo.comment.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>  {
    List<Comment> findByPostId(Long postId); // retorna todos os comentarios de um post
    long countByPostId(Long postId); // contar quantos comentarios um post tem
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable); // por data mais recente

    Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

    Page<Comment> findByParentCommentId(Long commentId, Pageable pageable);

    Optional<Comment> findByIdAndPostId(Long commentId, Long postId);

    boolean existsByParentCommentId(Long commentId);
} 

