package com.pivinadanang.blog.services.post;

import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.repositories.CategoryRepository;
import com.pivinadanang.blog.repositories.ImageRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    @Override
    @Transactional
    public PostResponse createPost(PostDTO postDTO) throws DataNotFoundException {
        // Kiểm tra và lấy thông tin CategoryEntity
        CategoryEntity existingCategory = categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " + postDTO.getCategoryId()));
        // Tạo PostEntity từ PostDTO và các thông tin liên quan
        PostEntity newPost = PostEntity.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .category(existingCategory)
                .thumbnail(postDTO.getThumbnail())
                .status(postDTO.getStatus())
                .build();
        // Lưu bài viết mới vào cơ sở dữ liệu
        PostEntity savedPostEntity = postRepository.save(newPost);
        return PostResponse.fromPost(savedPostEntity);
    }

    @Override
    public PostEntity getPostById(long postId) throws Exception {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find post with id " + postId));
    }

    @Override
    public Page<PostResponse> getAllPosts(String keyword, Long categoryId,PageRequest pageRequest) {
        // Lấy danh sách bài viết theo trang, giới hạn số lượng bài viết trên mỗi trang, categoryId nếu có
        Page<PostEntity> postsPage;
        postsPage = postRepository.searchPosts(categoryId, keyword, pageRequest);
        return postsPage.map(PostResponse::fromPost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(long id, UpdatePostDTO postDTO) throws Exception {
        // Lấy thông tin bài viết hiện có
        PostEntity existingPost = getPostById(id);
        // Lấy thông tin Category hiện có
        if(postDTO.getCategoryId() != null ){
            CategoryEntity existingCategory = categoryRepository.findById(postDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " + postDTO.getCategoryId()));
            existingPost.setCategory(existingCategory);

        }

        // Cập nhật thông tin bài viết
        // Cập nhật thông tin bài viết chỉ nếu không null hoặc không trống
        if (postDTO.getTitle() != null && !postDTO.getTitle().isEmpty()) {
            existingPost.setTitle(postDTO.getTitle());
        }

        if (postDTO.getContent() != null && !postDTO.getContent().isEmpty()) {
            existingPost.setContent(postDTO.getContent());
        }

        if (postDTO.getStatus() != null) {
            existingPost.setStatus(postDTO.getStatus());
        }

        if (postDTO.getThumbnail() != null && !postDTO.getThumbnail().isEmpty()) {
            existingPost.setThumbnail(postDTO.getThumbnail());
        }



        PostEntity updatedPost = postRepository.save(existingPost);

        return PostResponse.fromPost(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(long id) {
        Optional<PostEntity> optionalPost = postRepository.findById(id);
        optionalPost.ifPresent(postRepository::delete);
    }

    @Override
    public boolean existsPostByTitle(String title) {
        return postRepository.existsByTitle(title);
    }

    @Override
    public PostEntity getPostBySlug( String slug) throws DataNotFoundException {
        List<PostEntity> posts = postRepository.findPostsBySlug(slug);
        if (posts.isEmpty()) {
            throw new DataNotFoundException("Post not found with slug " + slug);
        }
        if (posts.size() > 1) {
            // Log or handle duplicates as necessary
            throw new DataNotFoundException("Multiple posts found with slug " + slug);
        }
        return posts.get(0);
    }

    @Override
    public List<PostResponse> getRecentPosts(int limit) {
        return  postRepository.findTopNRecentPosts(limit).stream()
                .map(PostResponse::fromPost)
                .collect(Collectors.toList());
    }

}
