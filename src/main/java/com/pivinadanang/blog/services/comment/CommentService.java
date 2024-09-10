package com.pivinadanang.blog.services.comment;

import com.github.javafaker.Faker;
import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.CommentRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
    public CommentEntity insertComment(CommentDTO commentDTO) {
        UserEntity user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        PostEntity post = postRepository.findById(commentDTO.getPostId()).orElse(null);
        if (user == null || post == null) {
            throw new IllegalArgumentException("User or product not found");
        }
        CommentEntity newComment = CommentEntity.builder()
                .user(user)
                .post(post)
                .content(commentDTO.getContent())
                .build();
        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
        CommentEntity existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        UserEntity user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        PostEntity post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new DataNotFoundException("Post not found"));

        existingComment.setContent(commentDTO.getContent());
        commentRepository.save(existingComment);
    }

    @Override
    public List<CommentResponse> getCommentsByUserAndPost(Long userId, Long productId) {
        List<CommentEntity> comments = commentRepository.findByUserIdAndPostId(userId, productId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long productId) {
        List<CommentEntity> comments = commentRepository.findByPostId(productId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }

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
    public CommentResponse getCommentById(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow( () -> new RuntimeException("Comment not found"));

        return CommentResponse.fromComment(comment);
    }

}
