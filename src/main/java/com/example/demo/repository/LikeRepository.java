package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId); // verifica se ja existe curtida do usuario naquele post, rettrona um boleano
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId); // busca a curtida em si, pode existir ou não um like

    long countByPostId(Long postId); // contar quantos likes um post tem
}
