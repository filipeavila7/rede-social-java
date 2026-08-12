package com.example.demo.profile.mapper;

import com.example.demo.profile.dto.ProfileResponse;
import com.example.demo.profile.entity.Profile;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProfileMapper {
    private final FileUrlUtils fileUrlUtils;

    public ProfileResponse toProfileResponse(Profile p){
        return new ProfileResponse(
                p.getUser().getId(),
                p.getUser().getName(),
                p.getBio(),
                fileUrlUtils.toPublicUrl(p.getImageUrlProfile()),
                p.getMessageStatus(),
                p.getUser().getUserName()

        );
    }
}
