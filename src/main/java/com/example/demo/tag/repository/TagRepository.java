package com.example.demo.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}