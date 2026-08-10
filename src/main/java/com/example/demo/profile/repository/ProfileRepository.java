package com.example.demo.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.profile.entity.Profile;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByUser_userNameContainingIgnoreCase(String userName);
}
