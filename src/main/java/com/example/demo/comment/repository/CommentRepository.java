package com.example.demo.comment.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.comment.entity.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>  {
    List<Comment> findByPostId(Long postId); // retorna todos os comentarios de um post
    long countByPostId(Long postId); // contar quantos comentarios um post tem
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable); // por data mais recente

    Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

    Page<Comment> findAllByUserId(Long userId, Pageable pageable);

    Page<Comment> findByParentCommentId(Long commentId, Pageable pageable);

    Optional<Comment> findByIdAndPostId(Long commentId, Long postId);

    boolean existsByParentCommentId(Long commentId);

    Page<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(
            Long postId,
            Pageable pageable
    );

    long countByParentCommentId(Long commentId);


    // comentarios normais
    @Query("""
    SELECT c
    FROM Comment c
    WHERE c.post.id = :postId
      AND c.parentComment IS NULL
    ORDER BY
        CASE WHEN c.user.id = :userId THEN 0 ELSE 1 END,
        c.createdAt DESC
""")
    Page<Comment> findPostCommentsPrioritized(
            @Param("postId") Long postId,
            @Param("userId") Long userId,
            Pageable pageable
    );


    // replys
    @Query("""
    SELECT c
    FROM Comment c
    WHERE c.parentComment.id = :commentId
    ORDER BY
        CASE WHEN c.user.id = :userId THEN 0 ELSE 1 END,
        c.createdAt DESC
""")
    Page<Comment> findCommentRepliesPrioritized(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId,
            Pageable pageable
    );



} 

