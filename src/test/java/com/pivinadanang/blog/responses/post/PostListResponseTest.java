package com.pivinadanang.blog.responses.post;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostListResponseTest {

    @Test
    public void testPostListResponse() {
        // Create a mock PostResponse
        PostResponse postResponse = PostResponse.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnailUrl("test-thumbnail-url")
                .publicId("test-public-id")
                .status("PUBLISHED")
                .authorName("Test Author")
                .profileImage("test-profile-image")
                .email("test@example.com")
                .commentCount(10)
                .ratingsCount(5)
                .viewCount(100)
                .visibility("PUBLIC")
                .revisionCount(2)
                .priority(1)
                .build();

        // Create a PostListResponse
        PostListResponse postListResponse = PostListResponse.builder()
                .posts(Collections.singletonList(postResponse))
                .totalPages(1)
                .status(HttpStatus.OK)
                .build();

        // Assertions
        assertEquals(1, postListResponse.getPosts().size());
        assertEquals(1, postListResponse.getTotalPages());
        assertEquals(HttpStatus.OK, postListResponse.getStatus());

        PostResponse response = postListResponse.getPosts().get(0);
        assertEquals(1L, response.getId());
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("test-title", response.getSlug());
        assertEquals("Test Excerpt", response.getExcerpt());
        assertEquals("test-thumbnail-url", response.getThumbnailUrl());
        assertEquals("test-public-id", response.getPublicId());
        assertEquals("PUBLISHED", response.getStatus());
        assertEquals("Test Author", response.getAuthorName());
        assertEquals("test-profile-image", response.getProfileImage());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(10, response.getCommentCount());
        assertEquals(5, response.getRatingsCount());
        assertEquals(100, response.getViewCount());
        assertEquals("PUBLIC", response.getVisibility());
        assertEquals(2, response.getRevisionCount());
        assertEquals(1, response.getPriority());
    }
}