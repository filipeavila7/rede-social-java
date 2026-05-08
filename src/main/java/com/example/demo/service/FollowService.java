package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.FollowingProfileResponse;
import com.example.demo.dto.NotificationRealtimeResponse;
import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import com.example.demo.repository.FollowRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    private final NotificationRepository notificationRepository;


    public FollowService(FollowRepository followRepository, UserRepository userRepository, WebSocketService webSocketService, NotificationRepository notificationRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
        this.notificationRepository = notificationRepository;
    }

    // seguir usuario pelo id dele
    public Follow followUser(Long followedId) { // passar o id do usuario que quer seguir

        // pegar email do usuario logado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // encontrar ele no banco pelo email logado
        User follower = userRepository.findByEmail(email);
        if (follower == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        // evitar o usuario seguir ele mesmo, caso o id dele seja igual ao passado no
        // argumento
        if (follower.getId().equals(followedId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode seguir a si mesmo");
        }

        // verifica se ele ja segue esse usuario
        if (followRepository.existsByFollowerIdAndFollowedId(follower.getId(), followedId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já segue esse usuário");
        }

        // encontrar no banco o usuario que vai ser seguido pelo id do argumento
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // objeto de novo seguidor, passando quem seguiu e quem esta sendo seguido
        Follow follow = new Follow(follower, followed);


        // salvar no banco
        Follow followSaved = followRepository.save(follow);

        // salva a notificação no banco
        Notification notification = new Notification();
        notification.setType("FOLLOW");
        notification.setContent(follower.getNome() + " seguiu você");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        notification.setSender(follower);
        notification.setReceiver(followed);
        notification.setPost(null);

        notificationRepository.save(notification);

        // cria a notificação para enviar via webSocket
        NotificationRealtimeResponse dto =
                new NotificationRealtimeResponse(
                        "FOLLOW",
                        follower.getId(),
                        follower.getNome(),
                        follower.getProfile() != null
                                ? follower.getProfile().getImageUrlProfile()
                                : null,
                        null,
                        null,
                        null,
                        notification.getContent(),
                        LocalDateTime.now()
                );

        // enviar notificação
        webSocketService.sendNotificationToUser(
                followedId,
                dto
        );



        return followSaved;

    }

    // deixar de seguir
    public void unfollowUser(Long followedId) {

        // pegar email do user logado
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User follower = userRepository.findByEmail(email);
        if (follower == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        // encoontrar seguidor pelo o id do user logado e o id do user que ele quer
        // deixar de seguir
        Follow follow = followRepository.findByFollowerIdAndFollowedId(follower.getId(), followedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow não encontrado"));

        followRepository.delete(follow);

    }

    // remover um seguidor
    public void removeFollower(Long followerId) {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User followed = userRepository.findByEmail(email);
        if (followed == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }
        // buscar seguidor paa remover com base no user logado
        Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, followed.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seguidor não encontrado"));

        followRepository.delete(follow);
    }

    // contagem de seguidores
    public long countFollowers(Long userId) {
        return followRepository.countByFollowedId(userId);
    }

    // contagem de seguindo
    public long countFollowing(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    // pegar seguidores e seguindo de outros seguidores
    public List<FollowingProfileResponse> getFollowing(Long userId) {
        return followRepository.findByFollowerId(userId)
                .stream()
                .map(Follow::getFollowed)
                .map(this::toFollowingProfileResponse)
                .toList();
    }

    public List<FollowingProfileResponse> getFollowers(Long userId) {
        return followRepository.findByFollowedId(userId)
                .stream()
                .map(Follow::getFollower)
                .map(this::toFollowingProfileResponse)
                .toList();
    }
    
    // pegar seguidores e seguindo do user logado
    public List<User> getMyFollowing() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User me = userRepository.findByEmail(email);
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        return followRepository.findByFollowerId(me.getId())
                .stream()
                .map(Follow::getFollowed)
                .toList();
    }

    public List<User> getMyFollowers() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User me = userRepository.findByEmail(email);
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        return followRepository.findByFollowedId(me.getId())
                .stream()
                .map(Follow::getFollower)
                .toList();
    }


    private FollowingProfileResponse toFollowingProfileResponse(User user) {
        return new FollowingProfileResponse(
                user.getId(),
                user.getNome(),
                user.getProfile() != null ? user.getProfile().getImageUrlProfile() : null,
                user.getProfile() != null ? user.getProfile().getMessageStatus() : null,
                user.getUserName()
        );
    }

    public boolean amIFollowing(Long followedId) {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User me = userRepository.findByEmail(email);

        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        return followRepository.existsByFollowerIdAndFollowedId(me.getId(), followedId);
    }

}
