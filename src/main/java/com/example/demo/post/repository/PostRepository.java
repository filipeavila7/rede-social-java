package com.example.demo.post.repository;

import com.example.demo.user.entity.User;
import com.example.demo.post.entity.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    // save()
    // findAll()
    // findById()
    // deleteById()


    @Query("""
       SELECT p FROM Post p
       WHERE p.createdAt >= :since
       ORDER BY p.createdAt DESC
        """)
        List<Post> findCandidatePosts(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("""
    SELECT DISTINCT p FROM Post p
    LEFT JOIN FETCH p.tags
    WHERE p IN :posts
    """)
    List<Post> findWithTagsFetched(@Param("posts") List<Post> posts);

    // buscar por content e tag
    Page<Post> findDistinctByTitleContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
            String content,
            String tagName,
            Pageable pageable
    );
    //sugestões
    List<Post> findTop8DistinctByTitleContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
            String content,
            String tagName
    );
    List<Post> findByUserIdOrderByCreatedAtDescIdDesc(Long userId); // retona os post de um usuario especifico
    Page<Post> findByUserUserNameOrderByCreatedAtDesc(String userName, Pageable pageable);
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Post> findByUserAndId(User user, Long postId);

    long countByUserId(Long userId); // retorna a quantidade de posts de um usuario
}
