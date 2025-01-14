package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.components.SecurityUtils;
import com.pivinadanang.blog.controller.CommentController;
import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.dtos.UpdateCommentDTO;
import com.pivinadanang.blog.enums.CommentStatus;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.comment.CommentListResponse;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import com.pivinadanang.blog.services.comment.ICommentService;
import com.pivinadanang.blog.services.user.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

public class CommentControllerTest {

    @Mock
    private ICommentService commentService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private IUserService userService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUser_NoComments() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        // Khi không có comment trong DB
        Page<CommentResponse> commentResponsePage = new PageImpl<>(Arrays.asList());  // Danh sách rỗng

        when(commentService.findAll("", null, pageable)).thenReturn(commentResponsePage);

        ResponseEntity<ResponseObject> responseEntity = commentController.getAllComment("", null, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get comments list successfully", responseEntity.getBody().getMessage());
        assertNotNull(responseEntity.getBody().getData());  // Kiểm tra không null
        assertTrue(((CommentListResponse) responseEntity.getBody().getData()).getComments().isEmpty());  // Kiểm tra danh sách rỗng
        assertEquals(0, ((CommentListResponse) responseEntity.getBody().getData()).getTotalPages());  // Kiểm tra tổng số trang là 0
    }

    @Test
    public void testGetCommentsByPostId() {
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CommentResponse> commentResponses = Arrays.asList(new CommentResponse(), new CommentResponse());

        when(commentService.getCommentsByPostId(postId, pageable)).thenReturn(commentResponses);

        ResponseEntity<ResponseObject> responseEntity = commentController.getCommentsByPostId(postId, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get comments successfully", responseEntity.getBody().getMessage());
        assertEquals(commentResponses, responseEntity.getBody().getData());
    }

    @Test
    public void testGetCommentsByPostId_NoComments() {
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CommentResponse> commentResponses = Arrays.asList();

        when(commentService.getCommentsByPostId(postId, pageable)).thenReturn(commentResponses);

        ResponseEntity<ResponseObject> responseEntity = commentController.getCommentsByPostId(postId, 0, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get comments successfully", responseEntity.getBody().getMessage());
        assertTrue(((List<CommentResponse>) responseEntity.getBody().getData()).isEmpty());
    }

    @Test
    public void testAddComment() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setUserId(1L);
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);
        CommentResponse commentResponse = new CommentResponse();
        when(commentService.insertComment(commentDTO)).thenReturn(commentResponse);

        ResponseEntity<ResponseObject> responseEntity = commentController.addComment(commentDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Insert comment successfully", responseEntity.getBody().getMessage());
        assertEquals(commentResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testAddComment_InvalidUser() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setUserId(2L);
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);

        ResponseEntity<ResponseObject> responseEntity = commentController.addComment(commentDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("You cannot comment as another user", responseEntity.getBody().getMessage());
    }

    @Test
    public void testUpdateComment() throws Exception {
        Long commentId = 1L;
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO();
        updateCommentDTO.setUserId(1L);
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);
        RoleEntity userRole = new RoleEntity();
        userRole.setName(RoleEntity.USER);
        loginUser.setRole(userRole);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);
        CommentResponse commentResponse = new CommentResponse();
        when(commentService.updateComment(commentId, updateCommentDTO)).thenReturn(commentResponse);

        ResponseEntity<ResponseObject> responseEntity = commentController.updateComment(commentId, updateCommentDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("update comment successfully", responseEntity.getBody().getMessage());
        assertEquals(commentResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testUpdateComment_InvalidUser() throws Exception {
        Long commentId = 1L;
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO();
        updateCommentDTO.setUserId(2L);
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);
        RoleEntity userRole = new RoleEntity();
        userRole.setName(RoleEntity.USER);
        loginUser.setRole(userRole);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);

        ResponseEntity<ResponseObject> responseEntity = commentController.updateComment(commentId, updateCommentDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("You cannot update another user's comment", responseEntity.getBody().getMessage());
    }

    @Test
    public void testDeleteComment() throws Exception {
        Long commentId = 1L;
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleEntity.ADMIN);
        loginUser.setRole(adminRole);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setUserId(1L);
        commentResponse.setUserRole(RoleEntity.ADMIN);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);
        when(commentService.getCommentById(commentId)).thenReturn(commentResponse);

        ResponseEntity<ResponseObject> responseEntity = commentController.deleteComment(commentId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete comment successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testDeleteComment_NotFound() throws Exception {
        Long commentId = 1L;

        when(commentService.getCommentById(commentId)).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = commentController.deleteComment(commentId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Comment not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testUpdateCommentStatus() throws Exception {
        Long commentId = 1L;
        CommentStatus status = CommentStatus.APPROVED;
        UserEntity loginUser = new UserEntity();
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleEntity.ADMIN);
        loginUser.setRole(adminRole);
        CommentResponse commentResponse = new CommentResponse();

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);
        when(commentService.getCommentById(commentId)).thenReturn(commentResponse);

        ResponseEntity<ResponseObject> responseEntity = commentController.updateCommentStatus(commentId, Map.of("status", status));

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update comment status successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testUpdateCommentStatus_NotFound() throws Exception {
        Long commentId = 1L;
        CommentStatus status = CommentStatus.APPROVED;
        UserEntity loginUser = new UserEntity();
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleEntity.ADMIN);
        loginUser.setRole(adminRole);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);
        when(commentService.getCommentById(commentId)).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = commentController.updateCommentStatus(commentId, Map.of("status", status));

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Comment not found", responseEntity.getBody().getMessage());
    }
    @Test
    public void testGetAllUser_InvalidPagination() throws Exception {
        ResponseEntity<ResponseObject> responseEntity = commentController.getAllComment("", null, -1, 10);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid pagination parameters", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetCommentsByPostId_InvalidPagination() {
        ResponseEntity<ResponseObject> responseEntity = commentController.getCommentsByPostId(1L, -1, 10);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid pagination parameters", responseEntity.getBody().getMessage());
    }

    @Test
    public void testAddComment_InvalidPayload() {
        CommentDTO commentDTO = new CommentDTO(); // Missing required fields
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);

        ResponseEntity<ResponseObject> responseEntity = commentController.addComment(commentDTO);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateComment_InvalidPayload() throws Exception {
        Long commentId = 1L;
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO(); // Missing required fields
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);
        RoleEntity userRole = new RoleEntity();
        userRole.setName(RoleEntity.USER);
        loginUser.setRole(userRole);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);

        ResponseEntity<ResponseObject> responseEntity = commentController.updateComment(commentId, updateCommentDTO);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateCommentStatus_InvalidStatus() throws Exception {
        Long commentId = 1L;
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleEntity.ADMIN);
        loginUser.setRole(adminRole);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);

        CommentResponse commentResponse = new CommentResponse();
        when(commentService.getCommentById(commentId)).thenReturn(commentResponse);

        Map<String, CommentStatus> request = new HashMap<>();
        request.put("status", null);

        ResponseEntity<ResponseObject> responseEntity = commentController.updateCommentStatus(commentId, request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid CommentStatus", responseEntity.getBody().getMessage());
    }
    @Test
    public void testDeleteComment_WithReplies() throws Exception {
        Long commentId = 1L;
        UserEntity loginUser = new UserEntity();
        loginUser.setId(1L);
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleEntity.ADMIN);
        loginUser.setRole(adminRole);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setUserId(1L);
        commentResponse.setUserRole(RoleEntity.ADMIN);

        when(securityUtils.getLoggedInUser()).thenReturn(loginUser);
        when(commentService.getCommentById(commentId)).thenReturn(commentResponse);

        ResponseEntity<ResponseObject> responseEntity = commentController.deleteComment(commentId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete comment successfully", responseEntity.getBody().getMessage());
    }


}