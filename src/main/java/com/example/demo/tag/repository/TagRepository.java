package com.example.demo.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.tag.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    public Optional<Tag> findByName(String name);
}