package com.example.demo.helpers;

import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.user.entity.User;
import com.example.demo.exeptions.post.PostConflictException;
import com.example.demo.exeptions.post.PostNotFoundException;
import com.example.demo.exeptions.user.UserNotFoundException;
import com.example.demo.post.entity.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


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

    // metodo para criar o objeto de notificações rapidamente
    public Notification buildNotification(
            User sender,
            User receiver,
            NotificationType type,
            String content
    ) {
        Notification notification = new Notification();

        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setType(type);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);

        return notification;
    }
}
