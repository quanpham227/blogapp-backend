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
        assertEquals("Get posts successfully", responseEntity.getBody().getMessage());
        assertEquals(postResponses, ((PostListResponse) responseEntity.getBody().getData()).getPosts());
    }
}