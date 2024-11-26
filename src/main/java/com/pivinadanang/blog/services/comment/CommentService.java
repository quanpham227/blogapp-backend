package com.pivinadanang.blog.services.comment;

import com.github.javafaker.Faker;
import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.enums.CommentStatus;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.CommentRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService{
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Override
    @Transactional
    public CommentResponse insertComment(CommentDTO commentDTO) {
        UserEntity user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        PostEntity post = postRepository.findById(commentDTO.getPostId()).orElse(null);
        if (user == null || post == null) {
            throw new IllegalArgumentException("User or post not found");
        }
        CommentStatus status = user.getRole().getName().equals(RoleEntity.ADMIN) ? CommentStatus.APPROVED : CommentStatus.PENDING;

        CommentEntity newComment = CommentEntity.builder()
                .user(user)
                .post(post)
                .content(commentDTO.getContent())
                .status(status)
                .parentComment(null) // Không có parent comment
                .build();
        CommentEntity comment = commentRepository.save(newComment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return CommentResponse.fromComment(comment);
    }
    @Override
    @Transactional
    public CommentResponse replyComment(CommentDTO commentDTO) {
        UserEntity user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        CommentEntity parentComment = commentRepository.findById(commentDTO.getParentCommentId()).orElse(null);
        if (user == null || parentComment == null) {
            throw new IllegalArgumentException("User or parent comment not found");
        }
        if (parentComment.getParentComment() != null) {
            throw new IllegalArgumentException("You can only reply to root comments.");
        }
        CommentStatus status = user.getRole().getName().equals(RoleEntity.ADMIN) ? CommentStatus.APPROVED : CommentStatus.PENDING;

        CommentEntity replyComment = CommentEntity.builder()
                .user(user)
                .post(parentComment.getPost())
                .content(commentDTO.getContent())
                .status(status)
                .parentComment(parentComment) // Thiết lập parent comment
                .build();
        CommentEntity comment = commentRepository.save(replyComment);
        // Tăng comment_count của bài viết
        PostEntity post = parentComment.getPost();
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return CommentResponse.fromComment(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) throws DataNotFoundException {
        Logger logger = LoggerFactory.getLogger(CommentService.class);

        // Tìm comment gốc (cha hoặc reply)
        logger.info("Attempting to delete comment with ID: {}", commentId);
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment with ID {} not found", commentId);
                    return new DataNotFoundException("Comment not found");
                });

        updateCommentAndRepliesStatus(comment, CommentStatus.DELETED);
        // Giảm comment_count của bài viết

    }

    @Transactional
    protected void updateCommentAndRepliesStatus(CommentEntity comment, CommentStatus newStatus) {
        Logger logger = LoggerFactory.getLogger(CommentService.class);

        // Cập nhật trạng thái bình luận
        logger.info("Updating status for comment ID: {}", comment.getId());
        CommentStatus oldStatus = comment.getStatus();
        comment.setStatus(newStatus);

        // Lấy danh sách bình luận con
        List<CommentEntity> replies = commentRepository.findByParentCommentId(comment.getId());
        logger.info("Found {} replies for comment ID: {}", replies.size(), comment.getId());

        for (CommentEntity reply : replies) {
            logger.info("Updating status for reply ID: {}", reply.getId());
            updateCommentAndRepliesStatus(reply, newStatus); // Gọi đệ quy để cập nhật tất cả reply
        }
        commentRepository.save(comment);
        logger.info("Comment ID: {} status updated to {}", comment.getId(), newStatus);
        // Cập nhật comment_count của bài viết
        PostEntity post = comment.getPost();
        if (oldStatus == CommentStatus.APPROVED && newStatus != CommentStatus.APPROVED) {
            if (post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
            }
        } else if (oldStatus != CommentStatus.APPROVED && newStatus == CommentStatus.APPROVED) {
            post.setCommentCount(post.getCommentCount() + 1);
        }
        postRepository.save(post);
    }


    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
        CommentEntity existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        existingComment.setContent(commentDTO.getContent());
        CommentEntity comment = commentRepository.save(existingComment);
        return CommentResponse.fromComment(comment);
    }


    @Override
    public List<CommentResponse> getCommentsByUserAndPost(Long userId, Long productId) {
        List<CommentEntity> comments = commentRepository.findByUserIdAndPostId(userId, productId);
        return comments.stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable) {
        List<CommentEntity> parentComments = commentRepository.findParentCommentsByPostIdAndStatus(postId, CommentStatus.APPROVED, pageable);
        List<Long> parentIds = parentComments.stream().map(CommentEntity::getId).collect(Collectors.toList());
        List<CommentEntity> replies = commentRepository.findRepliesByParentIdsAndStatus(parentIds, CommentStatus.APPROVED);

        // Tạo map để dễ dàng gán replies vào parent comments
        Map<Long, List<CommentEntity>> repliesMap = replies.stream().collect(Collectors.groupingBy(reply -> reply.getParentComment().getId()));

        // Gán replies vào parent comments
        for (CommentEntity parentComment : parentComments) {
            parentComment.setReplies(repliesMap.getOrDefault(parentComment.getId(), new ArrayList<>()));
        }

        return parentComments.stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
    }

// neeus dungf hamf loc
//    @Override
//    public List<CommentResponse> getCommentsByPostId(Long postId) {
//        List<CommentEntity> comments = commentRepository.findByPostIdAndStatus(postId, CommentStatus.APPROVED);
//        return comments.stream()
//                .map(comment -> filterApprovedReplies(comment))
//                .collect(Collectors.toList());
//    }
//
//    private CommentResponse filterApprovedReplies(CommentEntity comment) {
//        List<CommentEntity> approvedReplies = comment.getReplies().stream()
//                .filter(reply -> reply.getStatus() == CommentStatus.APPROVED)
//                .collect(Collectors.toList());
//        comment.setReplies(approvedReplies);
//        return CommentResponse.fromComment(comment);
//    }



    @Override
    //@Transactional
    public void generateFakeComments() throws Exception {
        Faker faker = new Faker();
        Random random = new Random();
        // Get all users with roleId = 1
        //List<User> users = userRepository.findByRoleId(1L);
        List<UserEntity> users = userRepository.findAll();
        // Get all products
        List<PostEntity> products = postRepository.findAll();
        List<CommentEntity> comments = new ArrayList<>();
        final int totalRecords = 10_000;
        final int batchSize = 1000;
        for (int i = 0; i < totalRecords; i++) {

            // Select a random user and product
            UserEntity user = users.get(random.nextInt(users.size()));
            PostEntity post = products.get(random.nextInt(products.size()));

            // Generate a fake comment
            CommentEntity comment = CommentEntity.builder()
                    .content(faker.lorem().sentence())
                    .post(post)
                    .user(user)
                    .build();

            // Set a random created date within the range of 2015 to now
            LocalDateTime startDate = LocalDateTime.of(2015, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.now();
            long randomEpoch = ThreadLocalRandom.current()
                    .nextLong(startDate.toEpochSecond(ZoneOffset.UTC), endDate.toEpochSecond(ZoneOffset.UTC));
            comment.setCreatedAt(LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC));
            // Save the comment
            comments.add(comment);
            if(comments.size() >= batchSize) {
                commentRepository.saveAll(comments);
                comments.clear();
            }
        }
    }

    @Override
    public CommentResponse getCommentById(Long commentId) throws DataNotFoundException {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow( () -> new DataNotFoundException("Comment not found"));

        return CommentResponse.fromComment(comment);
    }

}
