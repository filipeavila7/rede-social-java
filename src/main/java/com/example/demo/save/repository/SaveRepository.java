package com.example.demo.save.repository;

import com.example.demo.save.entity.Save;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaveRepository extends JpaRepository<Save, Long> {

    Page<Save> findByUserId(Long userId, Pageable pageable);

    Optional<Save> findByUserIdAndPostId(Long userId, Long postId);
}
