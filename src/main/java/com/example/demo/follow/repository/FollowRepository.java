package com.example.demo.follow.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.follow.entity.Follow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId); // verifca se ja existe seguidor
    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId); // busca o seguidor em si

    long countByFollowedId(Long userId);  // seguidores
    long countByFollowerId(Long userId);  // seguindo

    Page<Follow> findByFollowerId(Long followerId, Pageable pageable); // lista de seguidores
    Page<Follow> findByFollowedId(Long followedId, Pageable pageable); // lista de quem segue


    // novos, para uso em lote no feed
    @Query("SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId AND f.followed.id IN :ids")
    Set<Long> findFollowedIdsAmong(@Param("userId") Long userId, @Param("ids") Collection<Long> ids);

    @Query("SELECT f.follower.id FROM Follow f WHERE f.followed.id = :userId AND f.follower.id IN :ids")
    Set<Long> findFollowerIdsAmong(@Param("userId") Long userId, @Param("ids") Collection<Long> ids);


}
