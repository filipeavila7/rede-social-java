package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId); // verifca se ja existe seguidor
    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId); // busca o seguidor em si

    long countByFollowedId(Long userId);  // seguidores
    long countByFollowerId(Long userId);  // seguindo

    List<Follow> findByFollowerId(Long followerId); // lista de seguidores
    List<Follow> findByFollowedId(Long followedId); // lista de quem segue


}
