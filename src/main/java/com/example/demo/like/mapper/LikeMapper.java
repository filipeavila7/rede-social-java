package com.example.demo.like.mapper;

import com.example.demo.like.dto.LikeResponse;
import com.example.demo.like.entity.Like;
import com.example.demo.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeMapper {
    private final PostMapper postMapper;

    public LikeResponse toLikeResponse(Like l){
        return new LikeResponse(
                postMapper.toPostResponse(l.getPost()),
                l.getCreatedAt()
        );
    }


}
