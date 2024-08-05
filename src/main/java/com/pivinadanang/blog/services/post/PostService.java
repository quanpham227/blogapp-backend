package com.pivinadanang.blog.services.post;

import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.models.PostImageEntity;
import com.pivinadanang.blog.repositories.CategoryRepository;
import com.pivinadanang.blog.repositories.PostImageRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostImageRepository postImageRepository;
    @Override
    @Transactional
    public PostResponse createPost(PostDTO postDTO) throws DataNotFoundException {
        // Kiểm tra và lấy thông tin CategoryEntity
        CategoryEntity existingCategory = categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " + postDTO.getCategoryId()));

        // Tạo slug cho bài viết
        postDTO.generateSlug();

        // Tạo PostImageEntity từ PostImageDTO
        PostImageEntity postImageEntity = PostImageEntity.builder()
                .imageUrl(postDTO.getPostImage().getImageUrl())
                .fileId(postDTO.getPostImage().getFileId())
                .build();

        // Lưu PostImageEntity vào cơ sở dữ liệu trước
        PostImageEntity savedPostImageEntity = postImageRepository.save(postImageEntity);

        // Tạo PostEntity từ PostDTO và các thông tin liên quan
        PostEntity newPost = PostEntity.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .slug(postDTO.getSlug())
                .category(existingCategory)
                .image(savedPostImageEntity)  // Gán PostImageEntity đã lưu vào PostEntity
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
    public Page<PostResponse> getAllPosts(PageRequest pageRequest) {
        return postRepository.findAll(pageRequest)
                .map(PostResponse::fromPost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(long id, PostDTO postDTO) throws Exception {
        // Lấy thông tin bài viết hiện có
        PostEntity existingPost = getPostById(id);
        // Lấy thông tin Category hiện có
        CategoryEntity existingCategory = categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " + postDTO.getCategoryId()));
        // Cập nhật thông tin bài viết
        existingPost.setTitle(postDTO.getTitle());
        existingPost.setContent(postDTO.getContent());
        postDTO.generateSlug();
        existingPost.setSlug(postDTO.getSlug());
        existingPost.setCategory(existingCategory);

        // Cập nhật hoặc thêm mới PostImageEntity nếu có
        if (postDTO.getPostImage() != null) {
            PostImageEntity existingImage = existingPost.getImage();
            if (existingImage != null) {
                existingImage.setImageUrl(postDTO.getPostImage().getImageUrl());
                existingImage.setFileId(postDTO.getPostImage().getFileId());
                postImageRepository.save(existingImage);
            } else {
                PostImageEntity postImageEntity = PostImageEntity.builder()
                        .imageUrl(postDTO.getPostImage().getImageUrl())
                        .fileId(postDTO.getPostImage().getFileId())
                        .build();
                PostImageEntity savedPostImageEntity = postImageRepository.save(postImageEntity);
                existingPost.setImage(savedPostImageEntity);
            }
        }

        // Lưu thông tin bài viết cập nhật vào cơ sở dữ liệu
        PostEntity updatedPost = postRepository.save(existingPost);
        return PostResponse.fromPost(updatedPost);
    }

    @Override
    public void deletePost(long id) {
        Optional<PostEntity> optionalPost = postRepository.findById(id);
        optionalPost.ifPresent(postRepository::delete);
    }

    @Override
    public List<PostEntity> findPostsByIds(List<Long> postIds) {
        return List.of();
    }

    @Override
    public boolean existsPostByTitle(String title) {
        return postRepository.existsByTitle(title);
    }

    @Override
    public PostEntity getPostByTitle(String title) {
        return  postRepository.getPostByTitle(title);
    }

    @Override
    public List<PostEntity> findFavoritePostsByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Page<PostResponse> searchPosts(Long categoryId, String keyword, PageRequest pageRequest) {
        return null;
    }

    @Override
    public PostEntity getDetailPost(Long postId) {
        return null;
    }

}
