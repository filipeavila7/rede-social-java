package com.example.demo.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Commente; 

public interface CommentRepository extends JpaRepository<Commente, Long>  {
    List<Commente> findByPostId(Long postId); // retorna todos os comentarios de um post
    long countByPostId(Long postId); // contar quantos comentarios um post tem 
} 

