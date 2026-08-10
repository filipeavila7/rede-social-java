package com.example.demo.profile.mapper;

import com.example.demo.profile.dto.ProfileResponse;
import com.example.demo.profile.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {
    public ProfileResponse toProfileResponse(Profile p){
        return new ProfileResponse(
                p.getUser().getId(),
                p.getUser().getName(),
                p.getBio(),
                p.getImageUrlProfile(),
                p.getMessageStatus(),
                p.getUser().getUserName()

        );
    }
}
