package com.example.demo.closeFriends.service;

import com.example.demo.closeFriends.dto.CloseFriendsRequest;
import com.example.demo.closeFriends.dto.CloseFriendsResponse;
import com.example.demo.closeFriends.entity.CloseFriends;
import com.example.demo.closeFriends.mapper.CloseFriendsMapper;
import com.example.demo.closeFriends.repository.CloseFriendsRepository;
import com.example.demo.helpers.GlobalHelperService;
import com.example.demo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloseFriendsService {
    private final CloseFriendsRepository closeFriendsRepository;
    private final GlobalHelperService globalHelperService;
    private final CloseFriendsMapper closeFriendsMapper;

    // get de lista de melhores amigos do user logado
    public Page<CloseFriendsResponse> getMyCloseFriends(Pageable pageable){
        User loggedUser = globalHelperService.getLoggedUser();

        return closeFriendsRepository.findAllByUserId(loggedUser.getId(), pageable)
                .map(closeFriendsMapper::toCloseFriendsResponse);

    }



    // adcionar usuarios aos melhores amigos
    public List<CloseFriendsResponse> addUsersInCloseFriends(
            CloseFriendsRequest request
    ) {
        User loggedUser = globalHelperService.getLoggedUser();

        Set<Long> relatedUserIds = globalHelperService.getRelatedUserIds(
                loggedUser.getId(),
                request.userIds()
        );

        // Busca quais desses usuários já estão nos Close Friends
        Set<Long> existingFriendIds =
                closeFriendsRepository.findFriendIdsByUserIdAndFriendIdIn(
                        loggedUser.getId(),
                        relatedUserIds
                );

        // Remove quem já está nos Close Friends
        Set<Long> newFriendIds = relatedUserIds.stream()
                .filter(id -> !existingFriendIds.contains(id))
                .collect(Collectors.toSet());

        // Busca somente os usuários que ainda serão adicionados
        List<User> validUsers =
                globalHelperService.findAllUsersByIdIn(newFriendIds);

        // cria vinculo
        List<CloseFriends> closeFriends = validUsers.stream()
                .map(user -> new CloseFriends(
                        null,
                        loggedUser,
                        user,
                        null
                ))
                .toList();

        closeFriendsRepository.saveAll(closeFriends);

        return closeFriends.stream()
                .map(closeFriendsMapper::toCloseFriendsResponse)
                .toList();
    }


}
