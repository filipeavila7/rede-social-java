package com.example.demo.follow.service;


import java.util.List;

import com.example.demo.dto.FollowingProfileResponse;
import com.example.demo.exeptions.follow.FollowConflictException;
import com.example.demo.exeptions.user.UserNotFoundException;
import com.example.demo.follow.dto.FollowResponse;
import com.example.demo.follow.mapper.FollowMapper;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.follow.entity.Follow;
import com.example.demo.user.entity.User;
import com.example.demo.follow.repository.FollowRepository;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final GlobalHelperService globalHelperService;
    private final FollowMapper followMapper;

    // seguir usuario pelo id dele
    public FollowResponse followUser(Long followedId) { // passar o id do usuario que quer seguir
        // pegar user logado
        User loggedUser = globalHelperService.getLoggedUser();

        // evitar o usuario seguir ele mesmo
        if (loggedUser.getId().equals(followedId)) {
            throw new FollowConflictException("Você não pode seguir a si mesmo");
        }

        // verifica se ele ja segue esse usuario
        if (followRepository.existsByFollowerIdAndFollowedId(loggedUser.getId(), followedId)) {
            throw new FollowConflictException("Você ja segue esse usuário");
        }

        // encontrar o usuario seguido
        User followed = globalHelperService.findUserById(followedId);

        // cria o relacionamento
        Follow follow = new Follow();

        follow.setFollower(loggedUser);
        follow.setFollowed(followed);

        // conteydo da notificação
        String content = loggedUser.getName() + " começou a seguir você";

        // cria a notificação
        notificationService.createFollowNotification(
                loggedUser, followed, NotificationType.FOLLOW, content
        );

        return followMapper.toFollowResponse(followRepository.save(follow));

    }

    // deixar de seguir
    public void unfollowUser(Long followedId) {
        User loggedUser = globalHelperService.getLoggedUser();

        // acha o usuario seguido
        Follow follow = followRepository.findByFollowerIdAndFollowedId(loggedUser.getId(), followedId)
                .orElseThrow(UserNotFoundException::new);

        followRepository.delete(follow);

    }

    // remover um seguidor
    public void removeFollower(Long followerId) {
        User loggedUser = globalHelperService.getLoggedUser();

        // busca o seguidor
        Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, loggedUser.getId())
                .orElseThrow(UserNotFoundException::new);

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

    // pegar seguindo de outro usuario
    public Page<FollowingProfileResponse> getFollowing(Long userId, Pageable pageable) {
        return followRepository.findByFollowerId(userId, pageable)
                .map(followMapper::toFollowingProfileResponse);
    }

    // pegar seguidores de outro de usuario
    public Page<FollowingProfileResponse> getFollowers(Long userId, Pageable pageable) {
        return followRepository.findByFollowedId(userId, pageable)
                .map(followMapper::toFollowerProfileResponse);

    }

    // pegar seguindo do user logado
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

    // pegar seguidores do user logado
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
