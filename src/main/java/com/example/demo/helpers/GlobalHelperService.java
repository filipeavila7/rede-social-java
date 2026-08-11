package com.example.demo.helpers;

import com.example.demo.comment.entity.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.exeptions.comment.CommentConflictException;
import com.example.demo.exeptions.like.LikeConflictException;
import com.example.demo.exeptions.like.LikeNotFoundException;
import com.example.demo.like.entity.Like;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.profile.entity.Profile;
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
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;


    // TODO - provavelmente usar esse metodo na service de follow
    // Retorna o status apenas se estiver dentro de 24h.
    public String getActiveStatus(Profile profile) {
        String status = profile.getMessageStatus();
        if (status == null) return null;
        LocalDateTime createdAt = profile.getMessageStatusCreatedAt();
        if (createdAt == null) return null;
        return createdAt.isBefore(LocalDateTime.now().minusHours(24)) ? null : status;
    }


    // criar profile rapidamente
    public void createProfile(User user){
        user.setProfile(new Profile("", null, null, user));
    }

    // pegar usuario logado
    public User getLoggedUser() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

       return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findByUserName(String userName){
        return userRepository.findByuserName(userName)
                .orElseThrow(UserNotFoundException::new);
    }

    // validar se o post pertence ao usario logado
    public Post validatePostOwnership(Post post, User user){
       return postRepository.findByUserAndId(user, post.getId())
               .orElseThrow(()-> new PostConflictException("Esse post não pertence a você"));
    }

    // buscar post por id
    public Post findPostById(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    // verifica se o usuario é dono do comentário
    public Comment validateCommentOwnership(Long commentId, User user){
        return commentRepository.findByIdAndUserId(commentId, user.getId())
                .orElseThrow(()-> new CommentConflictException("Esse comentário não pertence a você"));
    }

    public Long countCommentBypostId(Long postId){
        return commentRepository.countByPostId(postId);
    }

    // verifica se existe registro de like no post
    public void verifyLikeInPost(Long userId, Long postId){
        if (likeRepository.existsByUserIdAndPostId(userId, postId)){
            throw new LikeConflictException("Você ja curtiu esse post");
        }
    }

    // retorna um boleano caso exista curtida ou não
    public boolean existsLikeInPost(Long userId, Long postId){
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    // retorna like existente
    public Like findLikeByUserIdAndPostId(Long userId, Long postId){
        return likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(LikeNotFoundException::new);
    }

    // contar quantos likes um post tem
    public Long countLikeByPostId(Long postId){
        return likeRepository.countByPostId(postId);
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
