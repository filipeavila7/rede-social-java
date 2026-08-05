package com.example.demo.helpers;

import com.example.demo.entity.User;
import com.example.demo.exeptions.post.PostConflictException;
import com.example.demo.exeptions.post.PostNotFoundException;
import com.example.demo.exeptions.user.UserNotFoundException;
import com.example.demo.post.entity.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GlobalHelperService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // pegar usuario logado
    public User getLoggedUser() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

       return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    // validar se o post pertence ao usario logado
    public Post validatePostOwnership(Post post, User user){
       return postRepository.findByUserAndId(user, post.getId())
               .orElseThrow(()-> new PostConflictException("Esse post não pertence a voçê"));
    }


    // buscar post por id
    public Post findPostById(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }
}
