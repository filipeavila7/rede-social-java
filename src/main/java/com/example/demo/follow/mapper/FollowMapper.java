package com.example.demo.follow.mapper;

import com.example.demo.dto.FollowingProfileResponse;
import com.example.demo.follow.dto.FollowResponse;
import com.example.demo.follow.entity.Follow;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.user.entity.User;
import com.example.demo.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowMapper {
    private final UserMapper userMapper;
    private final GlobalHelperService globalHelperService;

    public FollowResponse toFollowResponse(Follow f){
        return new FollowResponse(
                userMapper.toUserResponse(f.getFollowed()),
                f.getCreatedAt()
        );
    }

    public FollowingProfileResponse toFollowingProfileResponse(Follow f) {
        User user = f.getFollowed();

        return new FollowingProfileResponse(
                user.getId(),
                user.getName(),
                user.getProfile().getImageUrlProfile(),
                globalHelperService.getActiveStatus(user.getProfile()),
                user.getUserName()
        );
    }

    public FollowingProfileResponse toFollowerProfileResponse(Follow f) {
        User user = f.getFollower();

        return new FollowingProfileResponse(
                user.getId(),
                user.getName(),
                user.getProfile().getImageUrlProfile(),
                globalHelperService.getActiveStatus(user.getProfile()),
                user.getUserName()
        );
    }
}
