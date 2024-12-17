package com.pivinadanang.blog.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.controller.PostUserController;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.post.PostListResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.post.IPostService;
import com.pivinadanang.blog.services.post.PostRedisService;
import com.pivinadanang.blog.ultils.MessageKeys;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PostUserControllerTest {

    @Mock
    private IPostService postService;

    @Mock
    private LocalizationUtils localizationUtils;

    @Mock
    private PostRedisService postRedisService;

    @InjectMocks
    private PostUserController postUserController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchPosts() throws JsonProcessingException {
        Pageable pageable = PageRequest.of(0, 6);
        List<PostResponse> postResponses = Arrays.asList(new PostResponse(), new PostResponse());
        Page<PostResponse> postPage = new PageImpl<>(postResponses, pageable, postResponses.size());

        when(postRedisService.getAllPosts("", "", "", pageable)).thenReturn(null);
        when(postService.searchPosts("", "", "", pageable)).thenReturn(postPage);
        doNothing().when(postRedisService).saveAllPosts(postResponses, "", "", "", pageable);

        ResponseEntity<ResponseObject> responseEntity = postUserController.searchPosts("", "", "", 0, 6);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get  posts successfully", responseEntity.getBody().getMessage());
        assertEquals(postResponses, ((PostListResponse) responseEntity.getBody().getData()).getPosts());
    }

    @Test
    public void testGetRecentPosts() throws JsonProcessingException {
        Pageable pageable = PageRequest.of(0, 5);
        List<PostResponse> postResponses = Arrays.asList(new PostResponse(), new PostResponse());
        Page<PostResponse> postPage = new PageImpl<>(postResponses, pageable, postResponses.size());

        when(postRedisService.getRecentPosts(pageable)).thenReturn(null);
        when(postService.getRecentPosts(pageable)).thenReturn(postPage);
        doNothing().when(postRedisService).saveRecentPosts(postResponses, pageable);

        ResponseEntity<ResponseObject> responseEntity = postUserController.getRecentPosts(0, 5);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get recent posts successfully", responseEntity.getBody().getMessage());
        assertEquals(postResponses, ((PostListResponse) responseEntity.getBody().getData()).getPosts());
    }

    @Test
    public void testGetPostBySlug() throws Exception {
        String slug = "test-slug";
        PostResponse postResponse = new PostResponse();
        postResponse.setSlug(slug);

        when(postRedisService.getPostBySlug(slug)).thenReturn(null);
        when(postService.getPostBySlug(slug)).thenReturn(postResponse);
        doNothing().when(postRedisService).savePostBySlug(postResponse, slug);
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY)).thenReturn("Get post successfully");

        ResponseEntity<ResponseObject> responseEntity = postUserController.getPostBySlug(slug);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get post successfully", responseEntity.getBody().getMessage());
        assertEquals(postResponse, responseEntity.getBody().getData());
    }
}