package com.example.demo.feed.test;

import com.example.demo.feed.interest.entity.UserInterest;
import com.example.demo.feed.interest.repository.UserInterestRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.post.entity.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.tag.entity.Tag;
import com.example.demo.tag.repository.TagRepository;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void anonymousUser_canAccessFeed_withoutToken() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void anonymousUser_likedByMeIsNullForEveryPost() throws Exception {
        // arrange: cria pelo menos 1 post no banco de teste
        // ... setup de dados

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].likedByMe").value(nullValue()));
    }

    @Test
    void authenticatedUser_withHighInterestInTag_seesRelatedPostRankedHigher() throws Exception {
        User user = createUser("filipe@test.com");

        Tag javaTag = createTag("Java");
        Tag pythonTag = createTag("Python");

        createPost("Post Java", user, List.of(javaTag));
        createPost("Post Python", user, List.of(pythonTag));

        UserInterest interest = new UserInterest();
        interest.setUser(user);
        interest.setTag(javaTag);
        interest.setScore(100.0);
        interest.setUpdatedAt(LocalDateTime.now());
        userInterestRepository.save(interest);

        String token = jwtService.gerarToken(user);

        mockMvc.perform(get("/posts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Post Java"));
    }

    // helpers de setup omitidos — dependem de como você já cria dados de teste hoje
    // ---------- helpers ----------

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");
        user.setPassword(passwordEncoder.encode("password123"));
        return userRepository.save(user);
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    private Post createPost(String title, User author, List<Tag> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setUser(author);
        post.setImageUrl("http://example.com/image.jpg");
        post.setDescription("desc");
        post.setCreatedAt(LocalDateTime.now());
        post.setTags(tags);
        return postRepository.save(post);
    }

}