package com.example.demo.feed.test;

import com.example.demo.feed.interaction.entity.InteractionType;
import com.example.demo.feed.interest.entity.UserInterest;
import com.example.demo.feed.interest.repository.UserInterestRepository;
import com.example.demo.feed.interest.service.UserInterestService;
import com.example.demo.post.entity.Post;
import com.example.demo.tag.entity.Tag;
import com.example.demo.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInterestServiceTest {

    @Mock
    private UserInterestRepository userInterestRepository;

    @InjectMocks
    private UserInterestService userInterestService;

    private User user;
    private Post post;
    private Tag javaTag;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        javaTag = new Tag();
        javaTag.setId(10L);

        post = new Post();
        post.setId(100L);
        post.setTags(List.of(javaTag));
    }

    @Test
    void likeThenUnlike_returnsScoreToOriginalValue() {
        UserInterest interest = new UserInterest();
        interest.setUser(user);
        interest.setTag(javaTag);
        interest.setScore(20.0);

        when(userInterestRepository.findByUserIdAndTagIdIn(eq(1L), anyList()))
                .thenReturn(List.of(interest));

        // like
        userInterestService.applyDelta(user, post, UserInterestService.likeWeight());
        assertThat(interest.getScore()).isEqualTo(25.0);

        // unlike
        userInterestService.applyDelta(user, post, -UserInterestService.likeWeight());
        assertThat(interest.getScore()).isEqualTo(20.0);
    }

    @Test
    void applyDelta_neverGoesBelowZero() {
        UserInterest interest = new UserInterest();
        interest.setUser(user);
        interest.setTag(javaTag);
        interest.setScore(2.0);

        when(userInterestRepository.findByUserIdAndTagIdIn(eq(1L), anyList()))
                .thenReturn(List.of(interest));

        userInterestService.applyDelta(user, post, -UserInterestService.likeWeight());

        assertThat(interest.getScore()).isEqualTo(0.0);
    }

    @Test
    void registerInterest_view_shortDuration_doesNotCreateOrUpdateScore() {
        userInterestService.registerInterest(user, post, InteractionType.VIEW, 5);

        verifyNoInteractions(userInterestRepository);
    }

    @Test
    void registerInterest_view_longDuration_capsAtMaxWeight() {
        when(userInterestRepository.findByUserIdAndTagIdIn(eq(1L), anyList()))
                .thenReturn(List.of());

        ArgumentCaptor<UserInterest> captor = ArgumentCaptor.forClass(UserInterest.class);

        userInterestService.registerInterest(user, post, InteractionType.VIEW, 500);

        verify(userInterestRepository).save(captor.capture());
        assertThat(captor.getValue().getScore()).isEqualTo(5.0); // teto do peso de VIEW
    }
}