package com.example.demo.closeFriends.repository;

import com.example.demo.closeFriends.entity.CloseFriends;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CloseFriendsRepository extends JpaRepository<CloseFriends, Long> {

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    Page<CloseFriends> findAllByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT cf.friend.id
        FROM CloseFriends cf
        WHERE cf.user.id = :userId
        AND cf.friend.id IN :friendIds
        """)
    Set<Long> findFriendIdsByUserIdAndFriendIdIn(
            @Param("userId") Long userId,
            @Param("friendIds") Collection<Long> friendIds
    );


    @Query("""
        SELECT cf
        FROM CloseFriends cf
        WHERE cf.user.id = :userId
        AND cf.friend.id IN :friendIds
        """)
    List<CloseFriends> findAllByUserIdAndFriendIdIn(
            @Param("userId") Long userId,
            @Param("friendIds") Collection<Long> friendIds
    );
}
