package com.pivinadanang.blog.services.post;

import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.post.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IPostService {
    PostResponse  createPost(PostDTO postDTO) throws Exception;
    PostEntity getPostById(long id) throws Exception;
    Page<PostResponse> getAllPosts( PageRequest pageRequest);
    PostResponse updatePost(long id, PostDTO postDTO) throws Exception;
    void deletePost(long id);
    List<PostEntity> findPostsByIds(List<Long> postIds);
    boolean existsPostByTitle(String title);
    PostEntity getPostByTitle(String title);
    List<PostEntity> findFavoritePostsByUserId(Long userId);
    Page<PostResponse> searchPosts(Long categoryId, String keyword, PageRequest pageRequest);
    PostEntity getDetailPost(Long postId);

}
