package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.dto.PostResponse;
import com.example.demo.entity.Post;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;

class PostServiceTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createPostDefineCreatedAtAtual() {
        PostRepository postRepository = mock(PostRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LikeRepository likeRepository = mock(LikeRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);
        PostService service = new PostService(postRepository, userRepository, likeRepository, commentRepository);

        User user = new User("Nome", "user@email.com", "senha");
        Post post = new Post();
        post.setContent("conteudo");
        post.setImageUrl("imagem.png");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user@email.com", null));

        when(userRepository.findByEmail("user@email.com")).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post saved = service.createPost(post);

        assertEquals(user, saved.getUser());
        assertNotNull(saved.getCreatedAt());
        verify(postRepository).save(post);
    }

    @Test
    void getAllPostsMantemMesmaOrdemQuandoSeedForIgual() {
        PostRepository postRepository = mock(PostRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LikeRepository likeRepository = mock(LikeRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);
        PostService service = new PostService(postRepository, userRepository, likeRepository, commentRepository);

        List<Post> posts = List.of(
                buildPost(1L, "post-1"),
                buildPost(2L, "post-2"),
                buildPost(3L, "post-3"),
                buildPost(4L, "post-4"),
                buildPost(5L, "post-5"));
        when(postRepository.findAll()).thenReturn(posts);

        Page<PostResponse> primeiraBusca = service.getAllPosts(0, 5, 123L);
        Page<PostResponse> segundaBusca = service.getAllPosts(0, 5, 123L);

        assertEquals(postIds(primeiraBusca), postIds(segundaBusca));
        verify(postRepository, org.mockito.Mockito.times(2)).findAll();
    }

    @Test
    void getAllPostsMudaOrdemQuandoSeedForDiferente() {
        PostRepository postRepository = mock(PostRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LikeRepository likeRepository = mock(LikeRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);
        PostService service = new PostService(postRepository, userRepository, likeRepository, commentRepository);

        List<Post> posts = List.of(
                buildPost(1L, "post-1"),
                buildPost(2L, "post-2"),
                buildPost(3L, "post-3"),
                buildPost(4L, "post-4"),
                buildPost(5L, "post-5"),
                buildPost(6L, "post-6"),
                buildPost(7L, "post-7"),
                buildPost(8L, "post-8"));
        when(postRepository.findAll()).thenReturn(posts);

        Page<PostResponse> seedUm = service.getAllPosts(0, 8, 123L);
        Page<PostResponse> seedDois = service.getAllPosts(0, 8, 987L);

        assertNotEquals(postIds(seedUm), postIds(seedDois));
    }

    @Test
    void getAllPostsAplicaPaginacaoDepoisDaOrdenacaoPorSeed() {
        PostRepository postRepository = mock(PostRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LikeRepository likeRepository = mock(LikeRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);
        PostService service = new PostService(postRepository, userRepository, likeRepository, commentRepository);

        List<Post> posts = List.of(
                buildPost(1L, "post-1"),
                buildPost(2L, "post-2"),
                buildPost(3L, "post-3"),
                buildPost(4L, "post-4"),
                buildPost(5L, "post-5"),
                buildPost(6L, "post-6"));
        when(postRepository.findAll()).thenReturn(posts);

        Page<PostResponse> paginaZero = service.getAllPosts(0, 3, 555L);
        Page<PostResponse> paginaUm = service.getAllPosts(1, 3, 555L);

        assertEquals(3, paginaZero.getContent().size());
        assertEquals(3, paginaUm.getContent().size());
        assertEquals(6, paginaZero.getTotalElements());
        assertEquals(Collections.emptySet(),
                paginaZero.getContent().stream()
                        .map(PostResponse::id)
                        .filter(id -> postIds(paginaUm).contains(id))
                        .collect(Collectors.toSet()));
    }

    private List<Long> postIds(Page<PostResponse> page) {
        return page.getContent().stream()
                .map(PostResponse::id)
                .toList();
    }

    private Post buildPost(Long id, String content) {
        User user = new User("Nome " + id, "user" + id + "@email.com", "senha12345");
        ReflectionTestUtils.setField(user, "id", id);

        Profile profile = new Profile();
        profile.setImageUrlProfile("/files/profile-" + id + ".png");
        profile.setUser(user);
        user.setProfile(profile);

        Post post = new Post();
        ReflectionTestUtils.setField(post, "id", id);
        post.setContent(content);
        post.setImageUrl("/files/post-" + id + ".png");
        post.setDescription("descricao-" + id);
        post.setCreatedAt(LocalDateTime.now().minusDays(id));
        post.setUser(user);
        user.setPosts(List.of(post));
        user.setLikes(List.of());
        user.setComments(List.of());
        ReflectionTestUtils.setField(post, "likes", List.of());
        ReflectionTestUtils.setField(post, "comments", List.of());
        return post;
    }
}
