package com.example.demo.repository;

import com.example.demo.entity.Post;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // save()
    // findAll()
    // findById()
    // deleteById()

    // buscar por content e tag
    Page<Post> findDistinctByContentContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
            String content,
            String tagName,
            Pageable pageable
    );
    //sugestões
    List<Post> findTop8DistinctByContentContainingIgnoreCaseOrTagsNameContainingIgnoreCaseOrderByCreatedAtDesc(
            String content,
            String tagName
    );
    List<Post> findByUserIdOrderByCreatedAtDescIdDesc(Long userId); // retona os post de um usuario especifico
    Page<Post> findByUserUserNameOrderByCreatedAtDesc(String userName, Pageable pageable);
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    long countByUserId(Long userId); // retorna a quantidade de posts de um usuario
}
