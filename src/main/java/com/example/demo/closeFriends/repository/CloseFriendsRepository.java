package com.example.demo.closeFriends.repository;

import com.example.demo.closeFriends.entity.CloseFriends;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CloseFriendsRepository extends JpaRepository<CloseFriends, Long> {

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
}
