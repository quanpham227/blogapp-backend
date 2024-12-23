package com.pivinadanang.blog.repository;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.CategoryRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private CategoryEntity category;
    private UserEntity user;
    private PostEntity post;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(CategoryEntity.builder().name("Test Category").build());
        user = userRepository.save(UserEntity.builder().email("testuser@example.com").password("password").build());
        post = postRepository.save(PostEntity.builder()
                .title("Test Post")
                .content("This is a test post.")
                .slug("test-post")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .category(category)
                .user(user)
                .build());
    }

    @Test
    void testExistsByTitle() {
        boolean exists = postRepository.existsByTitle("Test Post");
        assertThat(exists).isTrue();
    }

    @Test
    void testFindByCategory() {
        List<PostEntity> posts = postRepository.findByCategory(category);
        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Test Post");
    }

    @Test
    void testFindPostById() {
        Optional<PostEntity> foundPost = postRepository.findPostById(post.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("Test Post");
    }

    @Test
    void testFindAllCreatedAt() {
        List<LocalDateTime> createdAtList = postRepository.findAllCreatedAt();
        assertThat(createdAtList).isNotEmpty();
    }

    @Test
    void testCount() {
        long count = postRepository.count();
        assertThat(count).isGreaterThan(0);
    }

    @Test
    void testFindMaxPriority() {
        int maxPriority = postRepository.findMaxPriority();
        assertThat(maxPriority).isEqualTo(0);
    }

    @Test
    void testSearchPostsForAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostEntity> result = postRepository.searchPostsForAdmin(0L, "Test", PostStatus.PUBLISHED, null, null, PostStatus.DELETED, pageable);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void testSearchDeletedPostsForAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostEntity> result = postRepository.searchDeletedPostsForAdmin(0L, "Test", PostStatus.DELETED, null, null, PostStatus.DELETED, pageable);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testSearchPostsForUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostEntity> result = postRepository.searchPostsForUser("Test", null, null, PostStatus.PUBLISHED, PostVisibility.PUBLIC, pageable);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void testFindRecentPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostEntity> result = postRepository.findRecentPosts(PostStatus.PUBLISHED, PostVisibility.PUBLIC, pageable);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void testFindPostBySlugAndStatusAndVisibility() {
        Optional<PostEntity> foundPost = postRepository.findPostBySlugAndStatusAndVisibility("test-post", PostStatus.PUBLISHED, PostVisibility.PUBLIC);
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("Test Post");
    }

    @Test
    void testFindTop3PostsExcludingStatus() {
        Pageable pageable = PageRequest.of(0, 3);
        List<PostEntity> topPosts = postRepository.findTop3PostsExcludingStatus(PostStatus.DELETED, pageable);
        assertThat(topPosts).hasSize(1);
    }

    @Test
    void testCountPageViewsPerDayLastWeek() {
        List<Long> pageViews = postRepository.countPageViewsPerDayLastWeek();
        assertThat(pageViews).isNotEmpty();
    }
}