package com.example.demo.tag.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.tag.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
     Optional<Tag> findByName(String name);


     // sugestão de tags
     @Query("""
    SELECT t
    FROM Tag t
    WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%'))
    ORDER BY t.name ASC
""")
     List<Tag> searchTagSuggestions(
             @Param("q") String q,
             Pageable pageable
     );
}