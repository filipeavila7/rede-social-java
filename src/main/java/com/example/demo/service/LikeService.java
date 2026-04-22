package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Like;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

@Service
public class LikeService {
    public final LikeRepository likeRepository;
    public final UserRepository userRepository;
    public final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }


    // curtir post pelo id do post com o user logado
    public Like likePost(Long postId){

        // pegar email do user logado
        String email = (String) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        // encontrar o post pelo id passado
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));


        // evitar like duplicado
        if (likeRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post já curtido");
        }

        // criar curtida fk key
        //like.getUser().getId() → vira user_id
        //like.getPost().getId() → vira post_id
        Like like = new Like(post, user);
        
        // salvar no banco
        return likeRepository.save(like);
        
        
    }

    // remover uma curtida
    public void unlikePost(Long postId){
        String email = (String) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        // pegar curtida existente pelo usuario logado no post curtido
        Like like = likeRepository.findByUserIdAndPostId(user.getId(), postId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curtida não encontrada"));

        likeRepository.delete(like);
    }

    // true ou false
    public boolean hasUserLikedPost(Long postId) {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        // verifica se existe like
        return likeRepository.existsByUserIdAndPostId(user.getId(), postId);
    }



    
}
