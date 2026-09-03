package com.example.demo.user.repository;

import com.example.demo.user.entity.User; // importar a tabela de usuario
import org.springframework.data.jpa.repository.JpaRepository; // importar a jparepository para herdar ela
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> { // passar classe do banco e o tipo do id 
    // permite ter metodos para fazer coisas na tabela de usuario como:
    // save()
    // findAll()
    // findById()
    // deleteById()

    @Query("SELECT u.id FROM User u WHERE u.id IN :userIds AND u.profile.privateProfile = true")
    Set<Long> findPrivateUserIds(@Param("userIds") Collection<Long> userIds);

    Optional<User>  findByEmail(String email); // procurar pelo email
    Optional<User> findByuserName(String userName);
    List<User> findByuserNameContainingIgnoreCase(String userName);
}
