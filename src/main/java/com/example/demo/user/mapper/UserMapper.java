package com.example.demo.user.mapper;

import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.entity.User;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final FileUrlUtils fileUrlUtils;

    public UserResponse toUserResponse(User u){
        return new UserResponse(
                u.getId(),
                u.getName(),
                fileUrlUtils.toPublicUrl(u.getProfile().getImageUrlProfile()),
                u.getUserName()
        );
    }
}
