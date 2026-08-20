package com.example.demo.save.mapper;

import com.example.demo.post.mapper.PostMapper;
import com.example.demo.save.dto.SaveResponse;
import com.example.demo.save.entity.Save;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveMapper {
    private final PostMapper postMapper;

    public SaveResponse toSaveResponse(Save s){
        return new SaveResponse(
               postMapper.toPostResponse(s.getPost())
        );
    }


}
