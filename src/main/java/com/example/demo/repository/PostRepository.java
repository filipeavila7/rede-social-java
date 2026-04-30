package com.example.demo.repository;

import com.example.demo.entity.Post;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // save()
    // findAll()
    // findById()
    // deleteById()

    //findByUserId(Long userId) significa:
    //“me traga todos os Post cujo campo user.id é esse valor”.
    List<Post> findAllByOrderByCreatedAtDescIdDesc();
    List<Post> findByUserIdOrderByCreatedAtDescIdDesc(Long userId); // retona os post de um usuario especifico
    List<Post> findByUserEmailOrderByCreatedAtDescIdDesc(String email); // retorna o email correspondente do usuario do post
    List<Post> findByUserUserNameOrderByCreatedAtDescIdDesc(String userName);
    long countByUserId(Long userId); // retorna a quantidade de posts de um usuario
}
