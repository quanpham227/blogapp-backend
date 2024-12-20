package com.pivinadanang.blog.responses.post;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.models.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PostResponseTest {

    @Test
    public void testFromPost() {
        // Mock PostEntity
        PostEntity postEntity = Mockito.mock(PostEntity.class);
        when(postEntity.getId()).thenReturn(1L);
        when(postEntity.getTitle()).thenReturn("Test Title");
        when(postEntity.getContent()).thenReturn("Test Content");
        when(postEntity.getSlug()).thenReturn("test-title");
        when(postEntity.getExcerpt()).thenReturn("Test Excerpt");
        when(postEntity.getThumbnail()).thenReturn("test-thumbnail-url");
        when(postEntity.getPublicId()).thenReturn("test-public-id");
        when(postEntity.getStatus()).thenReturn(PostStatus.PUBLISHED);
        when(postEntity.getVisibility()).thenReturn(PostVisibility.PUBLIC);
        when(postEntity.getRevisionCount()).thenReturn(2);
        when(postEntity.getViewCount()).thenReturn(100);
        when(postEntity.getRatingsCount()).thenReturn(5);
        when(postEntity.getCommentCount()).thenReturn(10);
        when(postEntity.getPriority()).thenReturn(1);

        // Mock CategoryEntity
        CategoryEntity categoryEntity = Mockito.mock(CategoryEntity.class);
        when(postEntity.getCategory()).thenReturn(categoryEntity);

        // Mock UserEntity
        UserEntity user = Mockito.mock(UserEntity.class);
        when(postEntity.getUser()).thenReturn(user);
        when(user.getFullName()).thenReturn("Test Author");
        when(user.getProfileImage()).thenReturn("test-profile-image");
        when(user.getEmail()).thenReturn("test@example.com");

        // Mock MetaEntity
        MetaEntity metaEntity = Mockito.mock(MetaEntity.class);
        when(postEntity.getMeta()).thenReturn(metaEntity);

        // Mock TagEntity
        TagEntity tagEntity = Mockito.mock(TagEntity.class);
        Set<TagEntity> tagEntities = new HashSet<>(Collections.singletonList(tagEntity));
        when(postEntity.getTags()).thenReturn(tagEntities);

        // Mock dates
        LocalDateTime now = LocalDateTime.now();
        when(postEntity.getCreatedAt()).thenReturn(now);
        when(postEntity.getUpdatedAt()).thenReturn(now);

        // Convert PostEntity to PostResponse
        PostResponse postResponse = PostResponse.fromPost(postEntity);

        // Assertions
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
        assertEquals("Test Author", postResponse.getAuthorName());
        assertEquals("test-profile-image", postResponse.getProfileImage());
        assertEquals("test@example.com", postResponse.getEmail());
        assertEquals(10, postResponse.getCommentCount());
        assertEquals(5, postResponse.getRatingsCount());
        assertEquals(100, postResponse.getViewCount());
        assertEquals(PostVisibility.PUBLIC.name(), postResponse.getVisibility());
        assertEquals(2, postResponse.getRevisionCount());
        assertEquals(1, postResponse.getPriority());
        assertEquals(now, postResponse.getCreatedAt());
        assertEquals(now, postResponse.getUpdatedAt());
    }
}