package com.example.demo.closeFriends.mapper;

import com.example.demo.closeFriends.dto.CloseFriendsResponse;
import com.example.demo.closeFriends.entity.CloseFriends;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloseFriendsMapper {

    private final UserMapper userMapper;

    public CloseFriendsResponse toCloseFriendsResponse(CloseFriends c){
        return new CloseFriendsResponse(
                c.getId(),
                userMapper.toUserResponse(c.getFriend()),
                c.getCreatedAt()
        );
    }
}
