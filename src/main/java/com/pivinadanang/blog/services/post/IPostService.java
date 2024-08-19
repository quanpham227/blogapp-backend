package com.pivinadanang.blog.services.post;

import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.post.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IPostService {
    PostResponse  createPost(PostDTO postDTO) throws Exception;
    PostEntity getPostById(long id) throws Exception;
    Page<PostResponse> getAllPosts( String keyword ,Long categoryId, PageRequest pageRequest);
    PostResponse updatePost(long id, UpdatePostDTO postDTO) throws Exception;
    void deletePost(long id);
    boolean existsPostByTitle(String title);
    PostEntity getPostBySlug( String slug) throws Exception;
    List<PostResponse> getRecentPosts(int limit);
}
