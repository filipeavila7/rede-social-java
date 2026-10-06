package com.example.demo.helpers;

import com.example.demo.closeFriends.repository.CloseFriendsRepository;
import com.example.demo.comment.entity.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.conversation.entity.Conversation;
import com.example.demo.conversation.repository.ConversationRepository;
import com.example.demo.exeptions.api.AccessDeniedException;
import com.example.demo.exeptions.comment.CommentConflictException;
import com.example.demo.exeptions.comment.CommentNotFoundException;
import com.example.demo.exeptions.like.LikeConflictException;
import com.example.demo.exeptions.like.LikeNotFoundException;
import com.example.demo.exeptions.profile.ProfileNotFoundException;
import com.example.demo.follow.repository.FollowRepository;
import com.example.demo.followRequest.entity.FollowRequest;
import com.example.demo.followRequest.entity.FollowRequestStatus;
import com.example.demo.followRequest.repository.FollowRequestRepository;
import com.example.demo.like.entity.Like;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.likeStory.repository.LikeStoryRepository;
import com.example.demo.notification.entity.Notification;
import com.example.demo.notification.entity.NotificationType;
import com.example.demo.profile.entity.Profile;
import com.example.demo.profile.repository.ProfileRepository;
import com.example.demo.save.repository.SaveRepository;
import com.example.demo.story.entity.Story;
import com.example.demo.story.entity.StoryVisibility;
import com.example.demo.story.repository.StoryRepository;
import com.example.demo.storyVisibilities.repository.StoryVisibilitiesRepository;
import com.example.demo.user.entity.User;
import com.example.demo.exeptions.post.PostConflictException;
import com.example.demo.exeptions.post.PostNotFoundException;
import com.example.demo.exeptions.user.UserNotFoundException;
import com.example.demo.post.entity.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GlobalHelperService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ProfileRepository profileRepository;
    private final ConversationRepository conversationRepository;
    private final FollowRepository followRepository;
    private final SaveRepository saveRepository;
    private final FollowRequestRepository followRequestRepository;
    private final CloseFriendsRepository closeFriendsRepository;
    private final StoryRepository storyRepository;
    private final StoryVisibilitiesRepository storyVisibilitiesRepository;
    private final LikeStoryRepository likeStoryRepository;


    // Retorna o status apenas se estiver dentro de 24h.
    public String getActiveStatus(Profile profile) {
        String status = profile.getMessageStatus();
        if (status == null) return null;
        LocalDateTime createdAt = profile.getMessageStatusCreatedAt();
        if (createdAt == null) return null;
        return createdAt.isBefore(LocalDateTime.now().minusHours(24)) ? null : status;
    }

    // procura uma conversation e caso não exista ele cria uma
    public Conversation findConversationOrNew(User sender, User receiver){
        return conversationRepository
                .findBetweenUsers(sender.getId(), receiver.getId())
                .orElseGet(() -> conversationRepository.save(
                        new Conversation(sender, receiver)
                ));
    }

    // pega conversation pelo id
    public Conversation getConversationById(Long conversationId){
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conversa não encontrada"
                ));
    }

    // busca varios usuarios pelo id
    public List<User> findAllUsersByIdIn(Collection<Long> ids){
        return userRepository.findAllByIdIn(ids);
    }

    // verifica se o usuario tem alguma relação de follow ou followwing com outros usuarios
    public Set<Long> getRelatedUserIds(Long loggedUserId, Collection<Long> userIds) {

        Set<Long> followingIds =
                followRepository.findFollowedIdsAmong(loggedUserId, userIds);

        Set<Long> followerIds =
                followRepository.findFollowerIdsAmong(loggedUserId, userIds);

        Set<Long> relatedIds = new HashSet<>(followingIds);
        relatedIds.addAll(followerIds);

        return relatedIds;
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

    // verificar se o userName ja existe
    public boolean validadeUserName(String userName){
        return userRepository.existsByUserNameAndIdNot(userName, this.getLoggedUser().getId());
    }

    public User getLoggedUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String email = auth.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public Profile getProfileByUserId(Long userId){
        return profileRepository.findByUserId(userId)
                .orElseThrow(ProfileNotFoundException::new);
    }

    // pegar user pelo id
    public User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    /// pegar user pelo yserName
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

    // contagem de post
    public long getPostsCountByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }

    // verifica se o usuario é dono do comentário
    public Comment validateCommentOwnership(Long commentId, User user){
        return commentRepository.findByIdAndUserId(commentId, user.getId())
                .orElseThrow(()-> new CommentConflictException("Esse comentário não pertence a você"));
    }

    // conta o numero de comentarios em um post
    public Long countCommentBypostId(Long postId){
        return commentRepository.countByPostId(postId);
    }


    public Comment findByCommentId(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    // verifica se existe registro de like no post
    public void verifyLikeInPost(Long userId, Long postId){
        if (likeRepository.existsByUserIdAndPostId(userId, postId)){
            throw new LikeConflictException("Você ja curtiu esse post");
        }
    }

    // retorna um boleano caso exista curtida ou não
    public boolean existsLikeInPost(Long postId){
        User loggedUser = this.getLoggedUser();
        return likeRepository.existsByUserIdAndPostId(loggedUser.getId(), postId);
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

    // contagem de seguidores
    public long countFollowers(Long userId) {
        return followRepository.countByFollowedId(userId);
    }

    // contagem de seguindo
    public long countFollowing(Long userId) {
        return followRepository.countByFollowerId(userId);
    }


    // TODO - metodo antigo, verifica se os 2 se seguem mas o ideal é apenas se o user logado esta seguindo o outro
    // verifica se os 2 usuarios se seguem
    public void validateCanViewProfile(Long profileUserId) {

        // pegar o logado
        Long loggedUserId = getLoggedUser().getId();

        // É o próprio perfil
        if (loggedUserId.equals(profileUserId)) {
            return;
        }

        User profileUser = userRepository.findById(profileUserId)
                .orElseThrow(UserNotFoundException::new);

        // Perfil público
        if (!profileUser.getProfile().isPrivateProfile()) {
            return;
        }

        // Perfil privado → precisa seguir mutuamente
        boolean followsOwner =
                followRepository.existsByFollowerIdAndFollowedId(
                        loggedUserId,
                        profileUserId
                );

        boolean ownerFollows =
                followRepository.existsByFollowerIdAndFollowedId(
                        profileUserId,
                        loggedUserId
                );

        // caso não se sigam, não deixa acessar
        if (!followsOwner || !ownerFollows) {
            throw new AccessDeniedException();
        }
    }



    // verifica se o usuário logado pode visualizar o perfil, ver posts, story
    public void validateCanViewPrivateProfile(Long profileUserId) {

        Long loggedUserId = getLoggedUser().getId();

        // É o próprio perfil
        if (loggedUserId.equals(profileUserId)) {
            return;
        }

        User profileUser = userRepository.findById(profileUserId)
                .orElseThrow(UserNotFoundException::new);

        // Perfil público
        if (!profileUser.getProfile().isPrivateProfile()) {
            return;
        }

        // Perfil privado → precisa seguir o dono
        boolean followsOwner =
                followRepository.existsByFollowerIdAndFollowedId(
                        loggedUserId,
                        profileUserId
                );

        if (!followsOwner) {
            throw new AccessDeniedException();
        }
    }




    // filtrar posts excluindo os privados
    public List<Post> filterVisible(List<Post> candidates, User viewer) {
        if (candidates.isEmpty()) return candidates;

        List<Long> authorIds = candidates.stream()
                .map(p -> p.getUser().getId())
                .distinct()
                .toList();

        Set<Long> privateAuthorIds = userRepository.findPrivateUserIds(authorIds);

        if (privateAuthorIds.isEmpty()) {
            return candidates;
        }

        if (viewer == null) {
            return candidates.stream()
                    .filter(p -> !privateAuthorIds.contains(p.getUser().getId()))
                    .toList();
        }

        Set<Long> othersPrivateIds = privateAuthorIds.stream()
                .filter(id -> !id.equals(viewer.getId()))
                .collect(Collectors.toSet());

        Set<Long> viewerFollows = followRepository.findFollowedIdsAmong(viewer.getId(), othersPrivateIds);
        Set<Long> followViewer = followRepository.findFollowerIdsAmong(viewer.getId(), othersPrivateIds);

        Set<Long> mutuallyVisible = new HashSet<>(viewerFollows);
        mutuallyVisible.retainAll(followViewer);

        return candidates.stream()
                .filter(p -> {
                    Long authorId = p.getUser().getId();
                    if (!privateAuthorIds.contains(authorId)) return true;
                    if (authorId.equals(viewer.getId())) return true;
                    return mutuallyVisible.contains(authorId);
                })
                .toList();
    }

    // boleano para verificar se o user segue o outro
    public boolean amIFollowing(Long followedId) {
        return followRepository.existsByFollowerIdAndFollowedId(
                this.getLoggedUser().getId(), followedId);
    }


    // retorna o tipo do request de pedido para seguir
    public FollowRequestStatus getFollowRequestStatus(Long userId) {
        User loggedUser = this.getLoggedUser();

        Optional<FollowRequest> existingRequest =
                followRequestRepository
                        .findByRequesterIdAndTargetIdAndStatus(
                                loggedUser.getId(),
                                userId,
                                FollowRequestStatus.PENDING
                        );

        return existingRequest
                .map(FollowRequest::getStatus)
                .orElse(null);
    }


    // verifica se o usuario está nos melhores amigos
    public boolean isCloseFriends(Long userId, Long friendId) {
        return closeFriendsRepository.existsByUserIdAndFriendId(
                userId,
                friendId
        );
    }


    // boleano para verificar se o post ja foi salvo
    public boolean saveByMe(Long postId){
        User loggedUser = this.getLoggedUser();
        return saveRepository.existsByUserIdAndPostId(loggedUser.getId(), postId);
    }



    // versão segura para visitante anônimo — não lança exceção
    public boolean existsLikeInPostOrDefault(Long postId){
        User loggedUser = this.getLoggedUserOrNull();
        if (loggedUser == null) {
            return false;
        }
        return likeRepository.existsByUserIdAndPostId(loggedUser.getId(), postId);
    }


    // versão segura para visitante anônimo — não lança exceção
    public boolean saveByMeOrDefault(Long postId){
        User loggedUser = this.getLoggedUserOrNull();
        if (loggedUser == null) {
            return false;
        }
        return saveRepository.existsByUserIdAndPostId(loggedUser.getId(), postId);
    }

    // valida story, melhores amigos e se o usuario segue
    public Story getStoryAndValidateAccess(Long storyId) {

        User loggedUser = this.getLoggedUser();

        // verifica se o story existe e ainda não expirou
        Story story = storyRepository.findByIdAndExpiresAtAfter(
                storyId,
                LocalDateTime.now()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Story não encontrado"
        ));

        // verifica acesso ao perfil privado
        if (story.getUser().getProfile().isPrivateProfile()) {
            this.validateCanViewPrivateProfile(
                    story.getUser().getId()
            );
        }

        // verifica acesso aos Close Friends
        if (story.getVisibility() == StoryVisibility.CLOSE_FRIENDS) {

            boolean isCloseFriend = this.isCloseFriends(
                    story.getUser().getId(),
                    loggedUser.getId()
            );

            if (!isCloseFriend) {
                throw new AccessDeniedException();
            }
        }

        return story;
    }

    // retorna quantidade de vizualizações um story tem
    public long countVisibilitiesByStoryId(Long storyId){
        return storyVisibilitiesRepository.countByStoryId(storyId);
    }

    // boleano para saber se o user logado curtiu o story
    public boolean isStoryLikedByMe(Long storyId){
        return likeStoryRepository.existsByStoryIdAndUserId(
                storyId, this.getLoggedUser().getId()
        );
    }

    public boolean isUserInCloseFriends(Long friendId){
        User loggedUser = this.getLoggedUserOrNull();
        if (loggedUser == null) {
            return false;
        }
        return closeFriendsRepository.existsByUserIdAndFriendId(loggedUser.getId(), friendId);
    }


    // verifica se existe story, valida disponível
    public boolean hasVisibleStory(Long userId) {
        User loggedUser = this.getLoggedUserOrNull();

        if (loggedUser == null) {
            return false;
        }

        // pega o user
        User user = this.findUserById(userId);

        // valida se o usuário pode acessar o perfil
        validateCanViewPrivateProfile(userId);

        // pega os stories do user
        List<Story> stories = storyRepository.
        findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
                        user.getUserName(),
                        LocalDateTime.now()
                );

        // valida os story
        return stories.stream()
                .anyMatch(this::canViewStory);
    }

    // verifica se tem story que o user ainda não viu
    public boolean hasUnviewedStory(Long userId) {
        User loggedUser = this.getLoggedUserOrNull();

        if (loggedUser == null) {
            return false;
        }

        // pega o user
        User user = this.findUserById(userId);

        // valida se o usuário pode acessar o perfil
        validateCanViewPrivateProfile(userId);

        // pega os stories do user
        List<Story> stories = storyRepository.
                findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
                        user.getUserName(),
                        LocalDateTime.now()
                );

        return stories.stream()
                .anyMatch(story ->
                        canViewStory(story)
                                && !storyVisibilitiesRepository
                                .existsByStoryIdAndUserId(
                                        story.getId(),
                                        loggedUser.getId()
                                )
                );
    }


    // verifica se o story ainda não visto é de mehlores amigos (importante para o front decidir a cor)
    public boolean hasUnviewedCloseFriendsStory(Long userId) {
        User loggedUser = this.getLoggedUserOrNull();

        if (loggedUser == null) {
            return false;
        }

        User user = this.findUserById(userId);

        validateCanViewPrivateProfile(userId);

        List<Story> stories = storyRepository
                .findByUserUserNameAndExpiresAtAfterOrderByCreatedAtAsc(
                        user.getUserName(),
                        LocalDateTime.now()
                );

        return stories.stream()
                .anyMatch(story ->
                        story.getVisibility() == StoryVisibility.CLOSE_FRIENDS
                                && (
                                story.getUser().getId().equals(loggedUser.getId())
                                        || canViewStory(story)
                        )
                                && !storyVisibilitiesRepository
                                .existsByStoryIdAndUserId(
                                        story.getId(),
                                        loggedUser.getId()
                                )
                );
    }

    // validar story
    public boolean canViewStory(Story story) {

        if (story.getVisibility() == StoryVisibility.EVERYONE) {
            return true;
        }

        if (story.getVisibility() == StoryVisibility.CLOSE_FRIENDS) {
            return isUserInCloseFriends(story.getUser().getId());
        }

        return false;
    }


    // verificar se existe vizualização no story
    public boolean isStoryViwed(Long storyId) {
        User loggedUser = this.getLoggedUserOrNull();

        if (loggedUser == null) {
            return false;
        }

        return storyVisibilitiesRepository.existsByStoryIdAndUserId(storyId, loggedUser.getId());
    }

}
