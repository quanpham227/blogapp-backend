package com.pivinadanang.blog.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.components.SecurityUtils;
import com.pivinadanang.blog.controller.PostAdminController;
import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.post.PostListResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.post.IPostService;
import com.pivinadanang.blog.ultils.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PostAdminControllerTest {

    @Mock
    private IPostService postService;

    @Mock
    private LocalizationUtils localizationUtils;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private PostAdminController postAdminController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testCreatePost() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Test Title");
        postDTO.setThumbnail("Test Thumbnail");
        postDTO.setPublicId("Test PublicId");

        when(postService.existsPostByTitle(postDTO.getTitle())).thenReturn(false);
        when(postService.createPost(postDTO)).thenReturn(new PostResponse());
        when(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_SUCCESSFULLY)).thenReturn("Insert post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.createPost(postDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Insert post successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetPostById() throws Exception {
        Long postId = 1L;
        PostResponse postResponse = new PostResponse();

        when(postService.getPostById(postId)).thenReturn(postResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY)).thenReturn("Get post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.getPostById(postId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get post successfully", responseEntity.getBody().getMessage());
        assertEquals(postResponse, responseEntity.getBody().getData());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testUpdatePost() throws Exception {
        Long postId = 1L;
        UpdatePostDTO updatePostDTO = new UpdatePostDTO();
        updatePostDTO.setTitle("Valid Title"); // Ensure the DTO has valid data
        PostResponse postResponse = new PostResponse();

        when(postService.updatePost(postId, updatePostDTO)).thenReturn(postResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY)).thenReturn("Update post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.updatePost(updatePostDTO, postId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update post successfully", responseEntity.getBody().getMessage());
        assertEquals(postResponse, responseEntity.getBody().getData());
    }
    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testDeleteOrDisablePost() throws Exception {
        Long postId = 1L;
        boolean isPermanent = true;
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loggedInUser);
        when(securityUtils.hasRole("ROLE_MODERATOR")).thenReturn(true);
        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_POST_SUCCESSFULLY)).thenReturn("Delete post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.deleteOrDisablePost(postId, isPermanent);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete post successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetPosts() throws JsonProcessingException {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<PostResponse> postResponses = Arrays.asList(new PostResponse(), new PostResponse());
        Page<PostResponse> postPage = new PageImpl<>(postResponses, pageRequest, postResponses.size());

        when(postService.getAllPosts("", 0L, null, null, null, pageRequest)).thenReturn(postPage);

        ResponseEntity<ResponseObject> responseEntity = postAdminController.getPosts("", 0L, 0, 10, null, null, null);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get posts successfully", responseEntity.getBody().getMessage());  // Kiểm tra thông điệp trả về
        assertEquals(postResponses, ((PostListResponse) responseEntity.getBody().getData()).getPosts());
    }
    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testCreatePost_TitleExists() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Existing Title");

        when(postService.existsPostByTitle(postDTO.getTitle())).thenReturn(true);
        when(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_ALREADY_EXISTS)).thenReturn("Post title already exists");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.createPost(postDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Post title already exists", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testCreatePost_MissingThumbnailOrPublicId() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Test Title");

        when(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_THUMBNAIL_REQUIRED)).thenReturn("Thumbnail or publicId is required");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.createPost(postDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Thumbnail or publicId is required", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testCreatePost_Exception() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("Test Title");
        postDTO.setThumbnail("Test Thumbnail");
        postDTO.setPublicId("Test PublicId");

        when(postService.createPost(postDTO)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<ResponseObject> responseEntity = postAdminController.createPost(postDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetPostById_NotFound() throws Exception {
        Long postId = 1L;

        when(postService.getPostById(postId)).thenThrow(new RuntimeException("Post not found"));
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY)).thenReturn("Get post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.getPostById(postId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Post not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetPostById_Exception() throws Exception {
        Long postId = 1L;

        when(postService.getPostById(postId)).thenThrow(new RuntimeException("Service error"));
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY)).thenReturn("Get post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.getPostById(postId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testUpdatePost_NotFound() throws Exception {
        Long postId = 1L;
        UpdatePostDTO updatePostDTO = new UpdatePostDTO();
        updatePostDTO.setTitle("Valid Title"); // Ensure the DTO has valid data

        when(postService.updatePost(postId, updatePostDTO)).thenThrow(new RuntimeException("Post not found"));
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY)).thenReturn("Update post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.updatePost(updatePostDTO, postId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Post not found", responseEntity.getBody().getMessage());
    }
    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testUpdatePost_InvalidDTO() throws Exception {
        Long postId = 1L;
        UpdatePostDTO updatePostDTO = new UpdatePostDTO();

        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY)).thenReturn("Update post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.updatePost(updatePostDTO, postId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testUpdatePost_Exception() throws Exception {
        Long postId = 1L;

        // Cung cấp giá trị hợp lệ cho UpdatePostDTO để vượt qua validation
        UpdatePostDTO updatePostDTO = new UpdatePostDTO();
        updatePostDTO.setTitle("Valid Title");

        when(postService.updatePost(postId, updatePostDTO)).thenThrow(new RuntimeException("Service error"));
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY)).thenReturn("Update post successfully");

        ResponseEntity<ResponseObject> responseEntity = postAdminController.updatePost(updatePostDTO, postId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }


    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testDeleteOrDisablePost_NotLoggedIn() throws Exception {
        Long postId = 1L;
        boolean isPermanent = true;

        when(securityUtils.getLoggedInUser()).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = postAdminController.deleteOrDisablePost(postId, isPermanent);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals("You must be logged in to perform this action.", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testDeleteOrDisablePost_NoPermission() throws Exception {
        Long postId = 1L;
        boolean isPermanent = true;
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loggedInUser);
        when(securityUtils.hasRole("ROLE_MODERATOR")).thenReturn(false);
        when(securityUtils.hasRole("ROLE_ADMIN")).thenReturn(false);

        ResponseEntity<ResponseObject> responseEntity = postAdminController.deleteOrDisablePost(postId, isPermanent);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals("You do not have permission to perform this action.", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteOrDisablePost_AdminDeletePermanently() throws Exception {
        Long postId = 1L;
        boolean isPermanent = true;
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loggedInUser);
        when(securityUtils.hasRole("ROLE_ADMIN")).thenReturn(true);

        ResponseEntity<ResponseObject> responseEntity = postAdminController.deleteOrDisablePost(postId, isPermanent);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals("Admins are not allowed to permanently delete posts.", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testDeleteOrDisablePost_NotFound() throws Exception {
        Long postId = 1L;
        boolean isPermanent = true;
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loggedInUser);
        when(securityUtils.hasRole("ROLE_MODERATOR")).thenReturn(true);
        doThrow(new RuntimeException("Post not found")).when(postService).deletePost(postId);

        ResponseEntity<ResponseObject> responseEntity = postAdminController.deleteOrDisablePost(postId, isPermanent);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Post not found", responseEntity.getBody().getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    public void testDeleteOrDisablePost_Exception() throws Exception {
        Long postId = 1L;
        boolean isPermanent = true;
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setId(1L);

        when(securityUtils.getLoggedInUser()).thenReturn(loggedInUser);
        when(securityUtils.hasRole("ROLE_MODERATOR")).thenReturn(true);
        doThrow(new RuntimeException("Service error")).when(postService).deletePost(postId);

        ResponseEntity<ResponseObject> responseEntity = postAdminController.deleteOrDisablePost(postId, isPermanent);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetPosts_InvalidPageRequest() throws JsonProcessingException {
        ResponseEntity<ResponseObject> responseEntity = postAdminController.getPosts("", 0L, -1, 10, null, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testGetPosts_Exception() throws JsonProcessingException {
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(postService.getAllPosts("", 0L, null, null, null, pageRequest)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<ResponseObject> responseEntity = postAdminController.getPosts("", 0L, 0, 10, null, null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetPosts_NoPosts() throws JsonProcessingException {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PostResponse> postPage = new PageImpl<>(Arrays.asList(), pageRequest, 0);

        when(postService.getAllPosts("", 0L, null, null, null, pageRequest)).thenReturn(postPage);

        ResponseEntity<ResponseObject> responseEntity = postAdminController.getPosts("", 0L, 0, 10, null, null, null);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get posts successfully", responseEntity.getBody().getMessage());
        assertEquals(0, ((PostListResponse) responseEntity.getBody().getData()).getPosts().size());
    }
}