package com.example.demo.profile.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.profile.entity.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Page<Profile> findByUser_userNameContainingIgnoreCase(String userName, Pageable pageable);
    Optional<Profile> findByUserId(Long userId);


    // buscar profiles pelo name ou username
    @Query("""
    SELECT p
    FROM Profile p
    JOIN p.user u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :q, '%'))
""")
    Page<Profile> searchProfiles(
            @Param("q") String q,
            Pageable pageable
    );

    // sugestão de profiles
    @Query("""
    SELECT p
    FROM Profile p
    JOIN p.user u
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :q, '%'))
""")
    List<Profile> searchProfileSuggestions(
            @Param("q") String q,
            Pageable pageable
    );
}
