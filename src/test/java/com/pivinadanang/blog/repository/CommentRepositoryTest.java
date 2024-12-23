package com.pivinadanang.blog.repository;

import com.pivinadanang.blog.enums.CommentStatus;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.CommentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private PostEntity post;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserEntity.builder().email("testuser@example.com").password("password").build());
        post = postRepository.save(PostEntity.builder().title("Test Post").user(user).publicId("test-public-id").slug("test-post").build());

        CommentEntity parentComment = CommentEntity.builder()
                .post(post)
                .user(user)
                .content("Parent Comment")
                .status(CommentStatus.APPROVED)
                .build();

        CommentEntity childComment = CommentEntity.builder()
                .post(post)
                .user(user)
                .content("Child Comment")
                .parentComment(parentComment)
                .status(CommentStatus.APPROVED)
                .build();

        commentRepository.saveAll(List.of(parentComment, childComment));
    }

    @Test
    void testFindParentCommentsByPostIdAndStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CommentEntity> parentComments = commentRepository.findParentCommentsByPostIdAndStatus(post.getId(), CommentStatus.APPROVED, pageable);
        assertThat(parentComments).hasSize(1);
        assertThat(parentComments.get(0).getContent()).isEqualTo("Parent Comment");
    }

    @Test
    void testFindRepliesByParentIdsAndStatus() {
        CommentEntity parentComment = commentRepository.findAll().get(0);
        List<CommentEntity> replies = commentRepository.findRepliesByParentIdsAndStatus(List.of(parentComment.getId()), CommentStatus.APPROVED);
        assertThat(replies).hasSize(1);
        assertThat(replies.get(0).getContent()).isEqualTo("Child Comment");
    }

    @Test
    void testGetAllComments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentEntity> comments = commentRepository.getAllComments("Comment", CommentStatus.APPROVED, CommentStatus.DELETED, pageable);
        assertThat(comments.getContent()).hasSize(2);
    }

    @Test
    void testCountTodayComments() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Long count = commentRepository.countTodayComments(startOfDay, endOfDay);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testCountCommentsPerDayLastWeek() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Object[]> counts = commentRepository.countCommentsPerDayLastWeek(startDate, endDate);
        assertThat(counts).isNotEmpty();
    }
}