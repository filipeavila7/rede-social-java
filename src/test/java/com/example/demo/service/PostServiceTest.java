package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.entity.Post;
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
    void getAllPostsRetornaOrdenadoPorDataMaisRecente() {
        PostRepository postRepository = mock(PostRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LikeRepository likeRepository = mock(LikeRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);
        PostService service = new PostService(postRepository, userRepository, likeRepository, commentRepository);

        Post maisRecente = new Post();
        maisRecente.setCreatedAt(LocalDateTime.now());

        Post maisAntigo = new Post();
        maisAntigo.setCreatedAt(LocalDateTime.now().minusDays(1));

        List<Post> ordenados = List.of(maisRecente, maisAntigo);
        when(postRepository.findAllByOrderByCreatedAtDescIdDesc()).thenReturn(ordenados);

        List<Post> resultado = service.getAllPosts();

        assertEquals(ordenados, resultado);
        assertEquals(maisRecente, resultado.get(0));
        verify(postRepository).findAllByOrderByCreatedAtDescIdDesc();
    }
}
