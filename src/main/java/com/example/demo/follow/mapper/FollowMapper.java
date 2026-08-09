package com.example.demo.follow.mapper;

import com.example.demo.follow.dto.FollowResponse;
import com.example.demo.follow.entity.Follow;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowMapper {
    private final UserMapper userMapper;

    public FollowResponse toFollowResponse(Follow f){
        return new FollowResponse(
                userMapper.toUserResponse(f.getFollowed()),
                f.getCreatedAt()
        );
    }
}
