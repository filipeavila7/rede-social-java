package com.example.demo.profile.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.profile.entity.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Page<Profile> findByUser_userNameContainingIgnoreCase(String userName, Pageable pageable);
    Optional<Profile> findByUserId(Long userId);
}
