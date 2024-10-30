package com.pivinadanang.blog.services.post;

import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.post.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface IPostService {
    //admin
    PostResponse  createPost(PostDTO postDTO) throws Exception;
    PostResponse getPostById(long id) throws Exception;
    Page<PostResponse> getAllPosts(String keyword , Long categoryId, PostStatus status, YearMonth createdAt, PageRequest pageRequest);
    PostResponse updatePost(long id, UpdatePostDTO postDTO) throws Exception;
    void deletePost(long id);
    void deletePosts(List<Long> ids);
    boolean existsPostByTitle(String title);
    Page<PostResponse> getRecentPosts(Pageable pageable);
    List<String> getAllMonthYears();
    Map<PostStatus, Long> getPostCountsByStatus();

    //user
    PostResponse getPostBySlug( String slug) throws Exception;
    Page<PostResponse> searchPosts(String keyword, String categorySlug, String tagSlug,  Pageable pageable);

}
