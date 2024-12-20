package com.pivinadanang.blog.services.comment;


import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.dtos.UpdateCommentDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private UserEntity user;
    private PostEntity post;
    private CommentEntity comment;
    private CommentDTO commentDTO;
    private UpdateCommentDTO updateCommentDTO;

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName(RoleEntity.USER);

        user = new UserEntity();
        user.setId(1L);
        user.setRole(role);

        post = new PostEntity();
        post.setId(1L);
        post.setCommentCount(0);

        comment = new CommentEntity();
        comment.setId(1L);
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent("Test comment");
        comment.setStatus(CommentStatus.APPROVED);

        commentDTO = new CommentDTO();
        commentDTO.setUserId(1L);
        commentDTO.setPostId(1L);
        commentDTO.setContent("Test comment");

        updateCommentDTO = new UpdateCommentDTO();
        updateCommentDTO.setContent("Updated comment");
    }
    @Test
    void testInsertComment_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        CommentResponse response = commentService.insertComment(commentDTO);

        assertNotNull(response);
        assertEquals("Test comment", response.getContent());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testInsertComment_UserOrPostNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.insertComment(commentDTO));
    }

    @Test
    void testReplyComment_Success() {
        CommentEntity parentComment = new CommentEntity();
        parentComment.setId(2L);
        parentComment.setUser(user);
        parentComment.setPost(post);
        parentComment.setContent("Parent comment");
        parentComment.setStatus(CommentStatus.APPROVED);

        commentDTO.setParentCommentId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findById(2L)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        CommentResponse response = commentService.replyComment(commentDTO);

        assertNotNull(response);
        assertEquals("Test comment", response.getContent());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testReplyComment_UserOrParentCommentNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> commentService.replyComment(commentDTO));
    }

    @Test
    void testDeleteComment_Success() throws DataNotFoundException {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> commentService.deleteComment(1L));
    }

    @Test
    void testUpdateComment_Success() throws DataNotFoundException {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        CommentResponse response = commentService.updateComment(1L, updateCommentDTO);

        assertNotNull(response);
        assertEquals("Updated comment", response.getContent());
    }

    @Test
    void testUpdateComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> commentService.updateComment(1L, updateCommentDTO));
    }

    @Test
    void testGetCommentsByUserAndPost() {
        List<CommentEntity> comments = new ArrayList<>();
        comments.add(comment);

        when(commentRepository.findByUserIdAndPostId(1L, 1L)).thenReturn(comments);

        List<CommentResponse> responses = commentService.getCommentsByUserAndPost(1L, 1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void testGetCommentsByPostId() {
        List<CommentEntity> parentComments = new ArrayList<>();
        parentComments.add(comment);

        when(commentRepository.findParentCommentsByPostIdAndStatus(1L, CommentStatus.APPROVED, Pageable.unpaged())).thenReturn(parentComments);
        when(commentRepository.findRepliesByParentIdsAndStatus(anyList(), eq(CommentStatus.APPROVED))).thenReturn(new ArrayList<>());

        List<CommentResponse> responses = commentService.getCommentsByPostId(1L, Pageable.unpaged());

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void testGetCommentById_Success() throws DataNotFoundException {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentResponse response = commentService.getCommentById(1L);

        assertNotNull(response);
        assertEquals("Test comment", response.getContent());
    }

    @Test
    void testGetCommentById_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    void testFindAll() throws Exception {
        List<CommentEntity> comments = new ArrayList<>();
        comments.add(comment);
        Page<CommentEntity> page = new PageImpl<>(comments);

        when(commentRepository.getAllComments(anyString(), eq(CommentStatus.APPROVED), eq(CommentStatus.DELETED), any(Pageable.class))).thenReturn(page);

        Page<CommentResponse> responses = commentService.findAll("keyword", CommentStatus.APPROVED, Pageable.unpaged());

        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
    }

    @Test
    void testUpdateCommentStatus_Success() throws Exception {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(comment);

        commentService.updateCommentStatus(1L, CommentStatus.DELETED);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testUpdateCommentStatus_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> commentService.updateCommentStatus(1L, CommentStatus.DELETED));
    }
}