package com.pivinadanang.blog.services.post;


import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.TagDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.*;
import com.pivinadanang.blog.repositories.*;
import com.pivinadanang.blog.responses.post.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostUtilityService postUtilityService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPostById_Success() throws Exception {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .revisionCount(2)
                .viewCount(100)
                .ratingsCount(5)
                .commentCount(10)
                .priority(1)
                .category(new CategoryEntity())
                .user(new UserEntity())
                .meta(new MetaEntity())
                .tags(new HashSet<>())
                .build();

        when(postRepository.findPostById(1L)).thenReturn(Optional.of(postEntity));

        // Call the service method
        PostResponse postResponse = postService.getPostById(1L);

        // Assertions
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
        assertEquals(10, postResponse.getCommentCount());
        assertEquals(5, postResponse.getRatingsCount());
        assertEquals(100, postResponse.getViewCount());
        assertEquals(PostVisibility.PUBLIC.name(), postResponse.getVisibility());
        assertEquals(2, postResponse.getRevisionCount());
        assertEquals(1, postResponse.getPriority());
    }

    @Test
    public void testGetPostById_NotFound() {
        when(postRepository.findPostById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            postService.getPostById(1L);
        });

        // Assertions
        assertEquals("Cannot find post with id 1", exception.getMessage());
    }
    @Test
    public void testGetAllPosts() {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .revisionCount(2)
                .viewCount(100)
                .ratingsCount(5)
                .commentCount(10)
                .priority(1)
                .category(new CategoryEntity())
                .user(new UserEntity())
                .meta(new MetaEntity())
                .tags(new HashSet<>())
                .build();

        Page<PostEntity> postPage = new PageImpl<>(Collections.singletonList(postEntity));
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(postRepository.searchPostsForAdmin(null, null, PostStatus.PUBLISHED, null, null, PostStatus.DELETED, pageRequest))
                .thenReturn(postPage);

        // Call the service method
        Page<PostResponse> postResponses = postService.getAllPosts(null, null, PostStatus.PUBLISHED, null, null, pageRequest);

        // Assertions
        assertEquals(1, postResponses.getTotalElements());
        PostResponse postResponse = postResponses.getContent().get(0);
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
        assertEquals(10, postResponse.getCommentCount());
        assertEquals(5, postResponse.getRatingsCount());
        assertEquals(100, postResponse.getViewCount());
        assertEquals(PostVisibility.PUBLIC.name(), postResponse.getVisibility());
        assertEquals(2, postResponse.getRevisionCount());
        assertEquals(1, postResponse.getPriority());
    }
    @Test
    public void testGetAllPosts_WithDateRange() {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .revisionCount(2)
                .viewCount(100)
                .ratingsCount(5)
                .commentCount(10)
                .priority(1)
                .category(new CategoryEntity())
                .user(new UserEntity())
                .meta(new MetaEntity())
                .tags(new HashSet<>())
                .build();

        Page<PostEntity> postPage = new PageImpl<>(Collections.singletonList(postEntity));
        PageRequest pageRequest = PageRequest.of(0, 10);

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        when(postRepository.searchPostsForAdmin(null, null, PostStatus.PUBLISHED, startDateTime, endDateTime, PostStatus.DELETED, pageRequest))
                .thenReturn(postPage);

        // Call the service method
        Page<PostResponse> postResponses = postService.getAllPosts(null, null, PostStatus.PUBLISHED, startDate, endDate, pageRequest);

        // Assertions
        assertEquals(1, postResponses.getTotalElements());
        PostResponse postResponse = postResponses.getContent().get(0);
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
        assertEquals(10, postResponse.getCommentCount());
        assertEquals(5, postResponse.getRatingsCount());
        assertEquals(100, postResponse.getViewCount());
        assertEquals(PostVisibility.PUBLIC.name(), postResponse.getVisibility());
        assertEquals(2, postResponse.getRevisionCount());
        assertEquals(1, postResponse.getPriority());
    }
    @Test
    public void testCreatePost_Success() throws DataNotFoundException {
        // Mock PostDTO
        PostDTO postDTO = PostDTO.builder()
                .title("Test Title")
                .content("Test Content")
                .categoryId(1L)
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .tags(Collections.singleton(new TagDTO("Test Tag")))
                .build();

        // Mock CategoryEntity
        CategoryEntity categoryEntity = new CategoryEntity();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));

        // Mock UserEntity
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        // Mock SecurityContextHolder
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(userDetails, null));

        // Mock PostUtilityService
        when(postUtilityService.generateSlug("Test Title")).thenReturn("test-title");
        when(postUtilityService.generateExcerpt("Test Content")).thenReturn("Test Excerpt");

        // Mock MetaEntity
        MetaEntity metaEntity = new MetaEntity();
        when(postUtilityService.generateMetaDescription("Test Content")).thenReturn("Test Meta Description");
        when(postUtilityService.generateOgDescription("Test Content")).thenReturn("Test OG Description");

        // Mock TagEntity
        TagEntity tagEntity = new TagEntity("Test Tag", "test-tag");
        when(tagRepository.findByName("Test Tag")).thenReturn(Optional.of(tagEntity));

        // Mock ImageEntity
        ImageEntity imageEntity = new ImageEntity();
        when(imageRepository.findByPublicId("test-public-id")).thenReturn(Optional.of(imageEntity));

        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .user(userEntity)
                .meta(metaEntity)
                .category(categoryEntity)
                .tags(new HashSet<>(Collections.singleton(tagEntity)))
                .build();

        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);

        // Call the service method
        PostResponse postResponse = postService.createPost(postDTO);

        // Assertions
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
    }
    @Test
    public void testCreatePost_CategoryNotFound() {
        // Mock PostDTO
        PostDTO postDTO = PostDTO.builder()
                .title("Test Title")
                .content("Test Content")
                .categoryId(1L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            postService.createPost(postDTO);
        });

        // Assertions
        assertEquals("Cannot find category with id 1", exception.getMessage());
    }
    @Test
    public void testUpdatePost_Success() throws Exception {
        // Mock UpdatePostDTO
        UpdatePostDTO postDTO = UpdatePostDTO.builder()
                .title("Updated Title")
                .content("Updated Content")
                .categoryId(1L)
                .thumbnail("updated-thumbnail-url")
                .publicId("updated-public-id")
                .status(PostStatus.PUBLISHED)
                .tags(Collections.singleton(new TagDTO("Updated Tag")))
                .build();

        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .category(new CategoryEntity())
                .user(new UserEntity())
                .meta(new MetaEntity())
                .tags(new HashSet<>())
                .build();

        when(postRepository.findPostById(1L)).thenReturn(Optional.of(postEntity));

        // Mock CategoryEntity
        CategoryEntity categoryEntity = new CategoryEntity();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));

        // Mock PostUtilityService
        when(postUtilityService.generateSlug("Updated Title")).thenReturn("updated-title");
        when(postUtilityService.generateExcerpt("Updated Content")).thenReturn("Updated Excerpt");

        // Mock TagEntity
        TagEntity tagEntity = new TagEntity("Updated Tag", "updated-tag");
        when(tagRepository.findByName("Updated Tag")).thenReturn(Optional.of(tagEntity));

        // Mock ImageEntity for both old and new publicId
        ImageEntity oldImageEntity = new ImageEntity();
        when(imageRepository.findByPublicId("test-public-id")).thenReturn(Optional.of(oldImageEntity));
        ImageEntity newImageEntity = new ImageEntity();
        when(imageRepository.findByPublicId("updated-public-id")).thenReturn(Optional.of(newImageEntity));

        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);

        // Call the service method
        PostResponse postResponse = postService.updatePost(1L, postDTO);

        // Assertions
        assertEquals(1L, postResponse.getId());
        assertEquals("Updated Title", postResponse.getTitle());
        assertEquals("Updated Content", postResponse.getContent());
        assertEquals("updated-title", postResponse.getSlug());
        assertEquals("Updated Excerpt", postResponse.getExcerpt());
        assertEquals("updated-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("updated-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
    }
    @Test
    public void testUpdatePost_NotFound() {
        // Mock UpdatePostDTO
        UpdatePostDTO postDTO = UpdatePostDTO.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(postRepository.findPostById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            postService.updatePost(1L, postDTO);
        });

        // Assertions
        assertEquals("Cannot find post with id 1", exception.getMessage());
    }
    @Test
    public void testDisablePost_Success() throws DataNotFoundException {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .status(PostStatus.PUBLISHED)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(postEntity));

        // Call the service method
        postService.disablePost(1L);

        // Assertions
        assertEquals(PostStatus.DELETED, postEntity.getStatus());
        verify(postRepository, times(1)).save(postEntity);
    }
    @Test
    public void testDisablePost_NotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            postService.disablePost(1L);
        });

        // Assertions
        assertEquals("Cannot find post with id 1", exception.getMessage());
    }
    @Test
    public void testDeletePost_Success() throws DataNotFoundException {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(postEntity));

        // Call the service method
        postService.deletePost(1L);

        // Verify the repository method was called
        verify(postRepository, times(1)).delete(postEntity);
    }

    @Test
    public void testDeletePost_NotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            postService.deletePost(1L);
        });

        // Assertions
        assertEquals("Cannot find post with id 1", exception.getMessage());
    }
    @Test
    public void testDeletePosts_Success() {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .status(PostStatus.PUBLISHED)
                .build();

        when(postRepository.findAllById(anyList())).thenReturn(Collections.singletonList(postEntity));

        // Call the service method
        postService.deletePosts(Collections.singletonList(1L));

        // Assertions
        assertEquals(PostStatus.DELETED, postEntity.getStatus());
        verify(postRepository, times(1)).saveAll(anyList());
    }
    @Test
    public void testExistsPostByTitle() {
        when(postRepository.existsByTitle("Test Title")).thenReturn(true);

        // Call the service method
        boolean exists = postService.existsPostByTitle("Test Title");

        // Assertions
        assertEquals(true, exists);
    }
    @Test
    public void testGetPostBySlug_Success() throws DataNotFoundException {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .revisionCount(2)
                .viewCount(100)
                .ratingsCount(5)
                .commentCount(10)
                .priority(1)
                .category(new CategoryEntity())
                .user(new UserEntity())
                .meta(new MetaEntity())
                .tags(new HashSet<>())
                .build();

        when(postRepository.findPostBySlugAndStatusAndVisibility("test-title", PostStatus.PUBLISHED, PostVisibility.PUBLIC))
                .thenReturn(Optional.of(postEntity));

        // Call the service method
        PostResponse postResponse = postService.getPostBySlug("test-title");

        // Assertions
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
        assertEquals(10, postResponse.getCommentCount());
        assertEquals(5, postResponse.getRatingsCount());
        assertEquals(101, postResponse.getViewCount()); // incremented by 1
        assertEquals(PostVisibility.PUBLIC.name(), postResponse.getVisibility());
        assertEquals(2, postResponse.getRevisionCount());
        assertEquals(1, postResponse.getPriority());
    }
    @Test
    public void testGetPostBySlug_NotFound() {
        when(postRepository.findPostBySlugAndStatusAndVisibility("test-title", PostStatus.PUBLISHED, PostVisibility.PUBLIC))
                .thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            postService.getPostBySlug("test-title");
        });

        // Assertions
        assertEquals("Cannot find post with slug test-title", exception.getMessage());
    }
    @Test
    public void testGetRecentPosts() {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .revisionCount(2)
                .viewCount(100)
                .ratingsCount(5)
                .commentCount(10)
                .priority(1)
                .category(new CategoryEntity())
                .user(new UserEntity())
                .meta(new MetaEntity())
                .tags(new HashSet<>())
                .build();

        Page<PostEntity> postPage = new PageImpl<>(Collections.singletonList(postEntity));
        Pageable pageable = PageRequest.of(0, 10);

        when(postRepository.findRecentPosts(PostStatus.PUBLISHED, PostVisibility.PUBLIC, pageable))
                .thenReturn(postPage);

        // Call the service method
        Page<PostResponse> postResponses = postService.getRecentPosts(pageable);

        // Assertions
        assertEquals(1, postResponses.getTotalElements());
        PostResponse postResponse = postResponses.getContent().get(0);
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
        assertEquals(10, postResponse.getCommentCount());
        assertEquals(5, postResponse.getRatingsCount());
        assertEquals(100, postResponse.getViewCount());
        assertEquals(PostVisibility.PUBLIC.name(), postResponse.getVisibility());
        assertEquals(2, postResponse.getRevisionCount());
        assertEquals(1, postResponse.getPriority());
    }
    @Test
    public void testSearchPosts() {
        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .slug("test-title")
                .excerpt("Test Excerpt")
                .thumbnail("test-thumbnail-url")
                .publicId("test-public-id")
                .status(PostStatus.PUBLISHED)
                .visibility(PostVisibility.PUBLIC)
                .revisionCount(2)
                .viewCount(100)
                .ratingsCount(5)
                .commentCount(10)
                .priority(1)
                .category(new CategoryEntity())
                .user(new UserEntity())
                .meta(new MetaEntity())
                .tags(new HashSet<>())
                .build();

        Page<PostEntity> postPage = new PageImpl<>(Collections.singletonList(postEntity));
        Pageable pageable = PageRequest.of(0, 10);

        when(postRepository.searchPostsForUser("keyword", "category-slug", "tag-slug", PostStatus.PUBLISHED, PostVisibility.PUBLIC, pageable))
                .thenReturn(postPage);

        // Call the service method
        Page<PostResponse> postResponses = postService.searchPosts("keyword", "category-slug", "tag-slug", pageable);

        // Assertions
        assertEquals(1, postResponses.getTotalElements());
        PostResponse postResponse = postResponses.getContent().get(0);
        assertEquals(1L, postResponse.getId());
        assertEquals("Test Title", postResponse.getTitle());
        assertEquals("Test Content", postResponse.getContent());
        assertEquals("test-title", postResponse.getSlug());
        assertEquals("Test Excerpt", postResponse.getExcerpt());
        assertEquals("test-thumbnail-url", postResponse.getThumbnailUrl());
        assertEquals("test-public-id", postResponse.getPublicId());
        assertEquals(PostStatus.PUBLISHED.name(), postResponse.getStatus());
        assertEquals(10, postResponse.getCommentCount());
        assertEquals(5, postResponse.getRatingsCount());
        assertEquals(100, postResponse.getViewCount());
        assertEquals(PostVisibility.PUBLIC.name(), postResponse.getVisibility());
        assertEquals(2, postResponse.getRevisionCount());
        assertEquals(1, postResponse.getPriority());
    }
}