package com.example.demo.save.repository;

import com.example.demo.save.entity.Save;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveRepository extends JpaRepository<Save, Long> {

    Page<Save> findByUserId(Long userId, Pageable pageable);
}
