package com.example.demo.user.mapper;

import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User u){
        return new UserResponse(
                u.getId(),
                u.getNome(),
                u.getProfile().getImageUrlProfile(),
                u.getUserName()
        );
    }
}
