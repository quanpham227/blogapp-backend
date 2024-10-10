package com.pivinadanang.blog.services.post;

import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.TagDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.*;
import com.pivinadanang.blog.repositories.*;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostUtilityService postUtilityService;
    private final FavoriteRepository favouriteRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    @Override
    @Transactional
    public PostResponse createPost(PostDTO postDTO) throws DataNotFoundException {
        // Kiểm tra và lấy thông tin CategoryEntity
        CategoryEntity existingCategory = categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " + postDTO.getCategoryId()));
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with email " + username));


        // Tạo MetaEntity từ các thuộc tính của PostEntity
        MetaEntity meta = MetaEntity.builder()
                .metaTitle(postDTO.getTitle())
                .metaDescription(postUtilityService.generateExcerpt(postDTO.getContent()))
                .metaKeywords(postUtilityService.generateKeywords(postDTO.getTitle()))
                .ogTitle(postDTO.getTitle())
                .ogDescription(postUtilityService.generateExcerpt(postDTO.getContent()))
                .ogImage(postDTO.getThumbnail())
                .build();
        // Tạo PostEntity từ PostDTO và các thông tin liên quan
        PostEntity newPost = PostEntity.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .slug(postUtilityService.generateSlug(postDTO.getTitle()))
                .excerpt(postUtilityService.generateExcerpt(postDTO.getContent()))
                .thumbnail(postDTO.getThumbnail())
                .status(postDTO.getStatus())
                .visibility(postDTO.getVisibility())
                .user(user)
                .meta(meta)
                .revisionCount(0)
                .viewCount(0)
                .ratingsCount(0)
                .commentCount(0)
                .category(existingCategory)
                .build();

        // Xử lý các thẻ (tags)
        Set<TagEntity> tags = new HashSet<>();
        for (TagDTO tagDTO : postDTO.getTags()) {
            TagEntity tag = tagRepository.findByName(tagDTO.getName())
                    .orElseGet(() -> tagRepository.save(new TagEntity(tagDTO.getName())));
            tags.add(tag);
        }
        newPost.setTags(tags);

        // Kiểm tra và cập nhật thuộc tính thumbnail và public_id
        if (postDTO.getPublicId() != null && !postDTO.getPublicId().isEmpty()) {
            ImageEntity imageEntity = imageRepository.findByPublicId(postDTO.getPublicId())
                    .orElseThrow(() -> new RuntimeException("Image not found"));

            // Cập nhật thuộc tính isUsed và usageCount
            imageEntity.setIsUsed(true);
            imageEntity.setUsageCount(imageEntity.getUsageCount() + 1);

            // Lưu thay đổi vào cơ sở dữ liệu
            imageRepository.save(imageEntity);
        }

        // Phân tích nội dung bài viết để tìm các URL hình ảnh
        List<String> imageUrls = extractImageUrls(postDTO.getContent());

        // Cập nhật trạng thái của các hình ảnh trong nội dung bài viết
        for (String imageUrl : imageUrls) {
            ImageEntity imageEntity = imageRepository.findByImageUrl(imageUrl)
                    .orElseThrow(() -> new RuntimeException("Image not found for URL: " + imageUrl));

            // Cập nhật thuộc tính isUsed và usageCount
            imageEntity.setIsUsed(true);
            imageEntity.setUsageCount(imageEntity.getUsageCount() + 1);

            // Lưu thay đổi vào cơ sở dữ liệu
            imageRepository.save(imageEntity);
        }
        // Lưu bài viết mới vào cơ sở dữ liệu
        PostEntity savedPostEntity = postRepository.save(newPost);
        return PostResponse.fromPost(savedPostEntity);
    }

    @Override
    public PostResponse getPostById(long postId) throws Exception {
        PostEntity post =   postRepository.findPostById(postId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find post with id " + postId));
        PostResponse postResponse = PostResponse.fromPost(post);
        return postResponse;
    }

    @Override
    public Page<PostResponse> getAllPosts(String keyword, Long categoryId, PostStatus status, YearMonth createdAt, PageRequest pageRequest) {
        // Chuyển YearMonth thành LocalDate với ngày bắt đầu và kết thúc của tháng đó
        LocalDateTime startDate = createdAt != null ? createdAt.atDay(1).atStartOfDay() : null;
        LocalDateTime endDate = createdAt != null ? createdAt.atEndOfMonth().atTime(23, 59, 59) : null;

        // Gọi phương thức searchPosts với startDate và endDate
        Page<PostEntity> postsPage = postRepository.searchPosts(categoryId, keyword, status, startDate, endDate, pageRequest);

        // Chuyển đổi từng PostEntity thành PostResponse và tính toán favoriteCount và commentCount
        return postsPage.map(post -> {
            // Tạo PostResponse từ PostEntity
            PostResponse postResponse = PostResponse.fromPost(post);


            return postResponse;
        });
    }



    @Override
    @Transactional
    public PostResponse updatePost(long id, UpdatePostDTO postDTO) throws Exception {
        // Lấy thông tin bài viết hiện có
        PostEntity existingPost = postRepository.findPostById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find post with id " + id));

        // Cập nhật Category nếu có
        if (postDTO.getCategoryId() != null) {
            CategoryEntity existingCategory = categoryRepository.findById(postDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find category with id " + postDTO.getCategoryId()));
            existingPost.setCategory(existingCategory);
        }

        // Cập nhật thông tin bài viết (title, content, status, thumbnail)
        if (postDTO.getTitle() != null && !postDTO.getTitle().isEmpty()) {
            existingPost.setTitle(postDTO.getTitle());
            existingPost.setSlug(postUtilityService.generateSlug(postDTO.getTitle())); // Cập nhật slug nếu tiêu đề thay đổi
        }

        if (postDTO.getContent() != null && !postDTO.getContent().isEmpty()) {
            existingPost.setContent(postDTO.getContent());
            existingPost.setExcerpt(postUtilityService.generateExcerpt(postDTO.getContent())); // Cập nhật excerpt
        }

        if (postDTO.getStatus() != null) {
            existingPost.setStatus(postDTO.getStatus());
        }

        if (postDTO.getThumbnail() != null && !postDTO.getThumbnail().isEmpty()) {
            // Kiểm tra và cập nhật trạng thái của hình ảnh cũ
            if (!postDTO.getThumbnail().equals(existingPost.getThumbnail())) {
                ImageEntity oldImageEntity = imageRepository.findByImageUrl(existingPost.getThumbnail())
                        .orElseThrow(() -> new RuntimeException("Old image not found"));
                oldImageEntity.setUsageCount(oldImageEntity.getUsageCount() - 1);
                if (oldImageEntity.getUsageCount() == 0) {
                    oldImageEntity.setIsUsed(false);
                }
                imageRepository.save(oldImageEntity);

                existingPost.setThumbnail(postDTO.getThumbnail());
            }
        }

        // Cập nhật thông tin Meta nếu có thay đổi
        MetaEntity meta = existingPost.getMeta();
        if (meta != null) {
            if (postDTO.getTitle() != null && !postDTO.getTitle().isEmpty()) {
                meta.setMetaTitle(postDTO.getTitle());
                meta.setOgTitle(postDTO.getTitle());
            }

            if (postDTO.getContent() != null && !postDTO.getContent().isEmpty()) {
                meta.setMetaDescription(postUtilityService.generateExcerpt(postDTO.getContent()));
                meta.setOgDescription(postUtilityService.generateExcerpt(postDTO.getContent()));
            }

            if (postDTO.getThumbnail() != null && !postDTO.getThumbnail().isEmpty()) {
                meta.setOgImage(postDTO.getThumbnail());
            }

            meta.setMetaKeywords(postUtilityService.generateKeywords(postDTO.getTitle()));
        }

        // Xử lý các thẻ (tags)
        if (postDTO.getTags() != null) {
            Set<TagEntity> tags = new HashSet<>();
            for (TagDTO tagDTO : postDTO.getTags()) {
                TagEntity tag = tagRepository.findByName(tagDTO.getName())
                        .orElseGet(() -> tagRepository.save(new TagEntity(tagDTO.getName())));
                tags.add(tag);
            }
            existingPost.setTags(tags);
        }

        // Kiểm tra và cập nhật thuộc tính thumbnail và public_id
        if (postDTO.getPublicId() != null && !postDTO.getPublicId().isEmpty()) {
            ImageEntity imageEntity = imageRepository.findByPublicId(postDTO.getPublicId())
                    .orElseThrow(() -> new RuntimeException("Image not found"));

            // Cập nhật thuộc tính isUsed và usageCount
            imageEntity.setIsUsed(true);
            imageEntity.setUsageCount(imageEntity.getUsageCount() + 1);

            // Lưu thay đổi vào cơ sở dữ liệu
            imageRepository.save(imageEntity);
        }

        // Phân tích nội dung bài viết để tìm các URL hình ảnh
        List<String> newImageUrls = extractImageUrls(postDTO.getContent());
        List<String> oldImageUrls = extractImageUrls(existingPost.getContent());

        // Cập nhật trạng thái của các hình ảnh trong nội dung bài viết
        for (String imageUrl : oldImageUrls) {
            if (!newImageUrls.contains(imageUrl)) {
                ImageEntity oldImageEntity = imageRepository.findByImageUrl(imageUrl)
                        .orElseThrow(() -> new RuntimeException("Old image not found for URL: " + imageUrl));
                oldImageEntity.setUsageCount(oldImageEntity.getUsageCount() - 1);
                if (oldImageEntity.getUsageCount() == 0) {
                    oldImageEntity.setIsUsed(false);
                }
                imageRepository.save(oldImageEntity);
            }
        }

        for (String imageUrl : newImageUrls) {
            ImageEntity newImageEntity = imageRepository.findByImageUrl(imageUrl)
                    .orElseThrow(() -> new RuntimeException("New image not found for URL: " + imageUrl));
            newImageEntity.setIsUsed(true);
            newImageEntity.setUsageCount(newImageEntity.getUsageCount() + 1);
            imageRepository.save(newImageEntity);
        }

        // Lưu bài viết đã cập nhật vào cơ sở dữ liệu
        PostEntity updatedPost = postRepository.save(existingPost);

        // Trả về đối tượng PostResponse
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
        return postRepository.findPostsBySlug(slug)
                .orElseThrow(() -> new DataNotFoundException("Cannot find post with slug " + slug));
    }

    @Override
    public Page<PostResponse> getRecentPosts(PageRequest pageRequest) {
        Page<PostEntity> postsPage = postRepository.findAll(pageRequest);
        return postsPage.map(PostResponse::fromPost);
    }

    @Override
    public List<String> getAllMonthYears() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return postRepository.findAllCreatedAt().stream()
                .map(date -> date.format(formatter))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<PostStatus, Long> getPostCountsByStatus() {
        Map<PostStatus, Long> postCounts = new HashMap<>();
        // Đếm số lượng bài viết theo từng trạng thái
        postCounts.put(PostStatus.published, postRepository.countByStatus(PostStatus.published));
        postCounts.put(PostStatus.draft, postRepository.countByStatus(PostStatus.draft));
        postCounts.put(PostStatus.deleted, postRepository.countByStatus(PostStatus.deleted));
        postCounts.put(PostStatus.pending, postRepository.countByStatus(PostStatus.pending));

        return postCounts;
    }
    // Phương thức để phân tích nội dung bài viết và tìm các URL hình ảnh
    private List<String> extractImageUrls(String content) {
        List<String> imageUrls = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            imageUrls.add(matcher.group(1));
        }
        return imageUrls;
    }

}
