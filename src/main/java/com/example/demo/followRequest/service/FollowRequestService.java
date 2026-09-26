package com.example.demo.followRequest.service;

import com.example.demo.exeptions.api.AccessDeniedException;
import com.example.demo.follow.entity.Follow;
import com.example.demo.follow.repository.FollowRepository;
import com.example.demo.followRequest.entity.FollowRequest;
import com.example.demo.followRequest.entity.FollowRequestStatus;
import com.example.demo.followRequest.repository.FollowRequestRepository;
import com.example.demo.helpers.GlobalHelperService;

import com.example.demo.notification.service.NotificationService;
import com.example.demo.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowRequestService {

    private final FollowRequestRepository followRequestRepository;
    private final NotificationService notificationService;
    private final GlobalHelperService globalHelperService;
    private final FollowRepository followRepository;

    // criar pedido
    public void createRequest(User requester, User target) {

        Optional<FollowRequest> pendingRequest =
                followRequestRepository
                        .findByRequesterIdAndTargetIdAndStatus(
                                requester.getId(),
                                target.getId(),
                                FollowRequestStatus.PENDING
                        );

        if (pendingRequest.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Você já enviou uma solicitação para esse usuário"
            );
        }

        FollowRequest request = new FollowRequest();

        request.setRequester(requester);
        request.setTarget(target);
        request.setStatus(FollowRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        FollowRequest saved = followRequestRepository.save(request);

        notificationService.createFollowRequestNotification(saved);
    }


    // TODO - criar notificação de follow tb
    // aceitar pedido para seguir
    @Transactional
    public void acceptRequest(Long requestId) {

        // encontra o pedido
        FollowRequest request = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada"
                ));

        User loggedUser = globalHelperService.getLoggedUser();

        // somente o dono do perfil pode aceitar
        if (!request.getTarget().getId().equals(loggedUser.getId())) {
            throw new AccessDeniedException();
        }

        // só pode aceitar uma solicitação pendente
        if (request.getStatus() != FollowRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Essa solicitação não está mais pendente"
            );
        }

        // cria o follow

        Follow follow = new Follow();

        follow.setFollower(request.getRequester());
        follow.setFollowed(request.getTarget());

        followRepository.save(follow);

        // mantém o request no banco para sabermos que foi aceito
        request.setStatus(FollowRequestStatus.ACCEPTED);

        followRequestRepository.save(request);
    }

    // recusar pedido
    @Transactional
    public void rejectRequest(Long requestId) {

        // busca o pedido
        FollowRequest request = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada"
                ));

        User loggedUser = globalHelperService.getLoggedUser();

        // somente o dono do perfil pode recusar
        if (!request.getTarget().getId().equals(loggedUser.getId())) {
            throw new AccessDeniedException();
        }

        // só pode recusar uma solicitação pendente
        if (request.getStatus() != FollowRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Essa solicitação não está mais pendente"
            );
        }

        // mantém o request no banco
        request.setStatus(FollowRequestStatus.REJECTED);

        followRequestRepository.save(request);
    }
}
