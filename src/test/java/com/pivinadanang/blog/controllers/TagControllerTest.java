package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.controller.TagController;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.tag.TagResponse;
import com.pivinadanang.blog.services.tag.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTopTags() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TagResponse> tagResponses = Arrays.asList(new TagResponse(), new TagResponse());

        when(tagService.getTopTags(pageable)).thenReturn(tagResponses);

        ResponseEntity<ResponseObject> responseEntity = tagController.getTopTags(10, 0);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get top tags successfully", responseEntity.getBody().getMessage());
        assertEquals(tagResponses, responseEntity.getBody().getData());
    }

    @Test
    public void testGetTopTagsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TagResponse> tagResponses = Collections.emptyList();

        when(tagService.getTopTags(pageable)).thenReturn(tagResponses);

        ResponseEntity<ResponseObject> responseEntity = tagController.getTopTags(10, 0);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get top tags successfully", responseEntity.getBody().getMessage());
        assertEquals(tagResponses, responseEntity.getBody().getData());
    }

    @Test
    public void testGetTopTagsInvalidPage() {
        ResponseEntity<ResponseObject> responseEntity = tagController.getTopTags(10, -1);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid limit or page", responseEntity.getBody().getMessage());
        assertEquals(null, responseEntity.getBody().getData());
    }

    @Test
    public void testGetTopTagsInvalidLimit() {
        ResponseEntity<ResponseObject> responseEntity = tagController.getTopTags(-10, 0);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid limit or page", responseEntity.getBody().getMessage());
        assertEquals(null, responseEntity.getBody().getData());
    }
    @Test
    public void testGetTopTagsLargePageNumber() {
        Pageable pageable = PageRequest.of(1000, 10);
        List<TagResponse> tagResponses = Collections.emptyList();

        when(tagService.getTopTags(pageable)).thenReturn(tagResponses);

        ResponseEntity<ResponseObject> responseEntity = tagController.getTopTags(10, 1000);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get top tags successfully", responseEntity.getBody().getMessage());
        assertEquals(tagResponses, responseEntity.getBody().getData());
    }

    @Test
    public void testGetTopTagsLargeLimit() {
        Pageable pageable = PageRequest.of(0, 1000);
        List<TagResponse> tagResponses = Arrays.asList(new TagResponse(), new TagResponse());

        when(tagService.getTopTags(pageable)).thenReturn(tagResponses);

        ResponseEntity<ResponseObject> responseEntity = tagController.getTopTags(1000, 0);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get top tags successfully", responseEntity.getBody().getMessage());
        assertEquals(tagResponses, responseEntity.getBody().getData());
    }

    @Test
    public void testGetTopTagsServiceException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(tagService.getTopTags(pageable)).thenThrow(new RuntimeException("Service exception"));

        ResponseEntity<ResponseObject> responseEntity = tagController.getTopTags(10, 0);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service exception", responseEntity.getBody().getMessage());
        assertEquals(null, responseEntity.getBody().getData());
    }
}