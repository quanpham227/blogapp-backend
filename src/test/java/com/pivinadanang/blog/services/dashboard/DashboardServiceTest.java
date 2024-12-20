package com.pivinadanang.blog.services.dashboard;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.repositories.CommentRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.responses.dashboard.DashboardResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private List<PostEntity> posts;
    private List<PostResponse> postResponses;
    private List<Object[]> rawData;

    @BeforeEach
    void setUp() {
        posts = new ArrayList<>();
        posts.add(new PostEntity());
        postResponses = new ArrayList<>();
        postResponses.add(PostResponse.fromPost(posts.get(0)));
        rawData = new ArrayList<>();
        rawData.add(new Object[]{java.sql.Date.valueOf("2023-10-01"), 5L});
    }

    @Test
    void testGetDashboardData() {
        when(postRepository.findTop3PostsExcludingStatus(eq(PostStatus.DELETED), any(Pageable.class)))
                .thenReturn(posts);
        when(postRepository.count()).thenReturn(10L);
        when(userRepository.countActiveUsers()).thenReturn(5L);
        when(commentRepository.countTodayComments(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(3L);
        when(commentRepository.countCommentsPerDayLastWeek(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(rawData);
        when(postRepository.countPageViewsPerDayLastWeek()).thenReturn(Arrays.asList(10L, 20L, 30L, 40L, 50L, 60L, 70L));

        DashboardResponse response = dashboardService.getDashboardData();

        assertNotNull(response);
        assertEquals(10L, response.getTotalPosts());
        assertEquals(5L, response.getActiveUsers());
        assertEquals(3L, response.getCommentsToday());
        assertEquals(1, response.getRecentPosts().size());
        assertEquals(7, response.getCommentsPerDayLastWeek().size());
        assertEquals(7, response.getPageViewsPerDayLastWeek().size());
    }
}