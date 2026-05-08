package com.example.demo.service;

import com.example.demo.dto.NotificationRealtimeResponse;
import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;
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

import java.time.LocalDateTime;

@Service
public class LikeService {
    public final LikeRepository likeRepository;
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final WebSocketService webSocketService;
    public final NotificationRepository notificationRepository;


    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository, WebSocketService webSocketService, NotificationRepository notificationRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.webSocketService = webSocketService;
        this.notificationRepository = notificationRepository;
    }


    // curtir post pelo id do post com o user logado
    public Like likePost(Long postId) {

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post não encontrado"
                ));

        if (likeRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Post já curtido");
        }

        Like like = new Like(post, user);
        Like likeSaved = likeRepository.save(like);

        // só notifica se não for o próprio post
        if (!post.getUser().getId().equals(user.getId())) {

            boolean alreadyExists = notificationRepository
                    .existsBySenderIdAndReceiverIdAndPostIdAndType(
                            user.getId(),
                            post.getUser().getId(),
                            post.getId(),
                            "LIKE"
                    );

            // salva a notificação no banco
            if (!alreadyExists) {
                Notification notification = new Notification();
                notification.setType("LIKE");
                notification.setContent(user.getNome() + " curtiu o seu post");
                notification.setCreatedAt(LocalDateTime.now());
                notification.setIsRead(false);
                notification.setSender(user);
                notification.setReceiver(post.getUser());
                notification.setPost(post);

                notificationRepository.save(notification);

                // cria a notificação para enviar via webSocket
                NotificationRealtimeResponse dto =
                        new NotificationRealtimeResponse(
                                "LIKE",
                                user.getId(),
                                user.getNome(),
                                user.getProfile() != null
                                        ? user.getProfile().getImageUrlProfile()
                                        : null,
                                postId,
                                null,
                                null,
                                notification.getContent(),
                                LocalDateTime.now()
                        );

                // enviar notificação
                webSocketService.sendNotificationToUser(
                        post.getUser().getId(),
                        dto
                );
            }
        }

        return likeSaved;
    }


        // remover uma curtida
        public void unlikePost (Long postId){
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
        public boolean hasUserLikedPost (Long postId){

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
