package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
