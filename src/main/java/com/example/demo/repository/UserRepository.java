package com.example.demo.repository;

import com.example.demo.entity.User; // importar a tabela de usuario
import org.springframework.data.jpa.repository.JpaRepository; // importar a jparepository para herdar ela

public interface UserRepository extends JpaRepository<User, Long> { // passar classe do banco e o tipo do id 
    // permite ter metodos para fazer coisas na tabela de usuario como:
    // save()
    // findAll()
    // findById()
    // deleteById()

    User findByEmail(String email); // procurar pelo email
}
