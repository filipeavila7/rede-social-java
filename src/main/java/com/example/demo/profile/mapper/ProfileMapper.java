package com.example.demo.profile.mapper;

import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.profile.dto.ProfileResponse;
import com.example.demo.profile.entity.Profile;
import com.example.demo.util.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProfileMapper {
    private final FileUrlUtils fileUrlUtils;
    private final GlobalHelperService globalHelperService;

    public ProfileResponse toProfileResponse(Profile p){
        return new ProfileResponse(
                p.getUser().getId(),
                p.getUser().getName(),
                p.getBio(),
                fileUrlUtils.toPublicUrl(p.getImageUrlProfile()),
                p.getMessageStatus(),
                p.getUser().getUserName(),
                globalHelperService.countFollowing(p.getUser().getId()),
                globalHelperService.countFollowers(p.getUser().getId()),
                globalHelperService.getPostsCountByUserId(p.getUser().getId())
        );
    }
}
