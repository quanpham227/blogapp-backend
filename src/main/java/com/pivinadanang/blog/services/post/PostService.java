package com.pivinadanang.blog.services.post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.TagDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.*;
import com.pivinadanang.blog.repositories.*;
import com.pivinadanang.blog.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;


import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    @Value("${app.base-url}")
    private String baseUrl;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostUtilityService postUtilityService;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;



    @Override
    public PostResponse getPostById(long postId) throws Exception {
        PostEntity post =   postRepository.findPostById(postId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find post with id " + postId));
        return PostResponse.fromPost(post);
    }

    @Override
    public Page<PostResponse> getAllPosts(String keyword, Long categoryId, PostStatus status, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        Page<PostEntity> postsPage;
        // Chuyển YearMonth thành LocalDate với ngày bắt đầu và kết thúc của tháng đó
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        // Gọi phương thức searchPosts với startDate và endDate
        if(status == PostStatus.DELETED){
           postsPage = postRepository.searchDeletedPostsForAdmin(categoryId, keyword, status, startDateTime, endDateTime,PostStatus.DELETED, pageRequest);

        } else {
            postsPage = postRepository.searchPostsForAdmin(categoryId, keyword, status, startDateTime, endDateTime,PostStatus.DELETED, pageRequest);

        }

        // Chuyển đổi từng PostEntity thành PostResponse
        return postsPage.map(post -> {
            // Tạo PostResponse từ PostEntity
            PostResponse postResponse = PostResponse.fromPost(post);


            return postResponse;
        });
    }

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
        MetaEntity meta = createMetaEntity(postDTO);
        // Tạo PostEntity từ PostDTO và các thông tin liên quan
        PostEntity newPost = PostEntity.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .slug(postUtilityService.generateSlug(postDTO.getTitle()))
                .excerpt(postUtilityService.generateExcerpt(postDTO.getContent()))
                .thumbnail(postDTO.getThumbnail())
                .publicId(postDTO.getPublicId())
                .status(postDTO.getStatus())
                .visibility(postDTO.getVisibility())
                .user(user)
                .meta(meta)
                .revisionCount(0)
                .viewCount(0)
                .ratingsCount(0)
                .commentCount(0)
                .priority(0)
                .category(existingCategory)
                .build();

        // Xử lý các thẻ (tags)
        Set<TagEntity> tags = processTags(postDTO.getTags());
        newPost.setTags(tags);
        // Logic 1: Cập nhật trạng thái của thumbnail và publicId
        // Logic 1: Cập nhật trạng thái của thumbnail và publicId
        if (postDTO.getPublicId() != null && !postDTO.getPublicId().isEmpty()) {
            updateImageUsageByPublicId(postDTO.getPublicId(), true, true);
        }

        // Logic 2: Trích xuất các URL trong phần nội dung của bài viết
        List<String> publicIds = extractPublicIdsFromImageUrls(postDTO.getContent());
        if (publicIds != null && !publicIds.isEmpty()) {
            for (String publicId : publicIds) {
                updateImageUsageByPublicId(publicId, true, false);
            }
        }
        // Lưu bài viết mới vào cơ sở dữ liệu
        PostEntity savedPostEntity = postRepository.save(newPost);
        return PostResponse.fromPost(savedPostEntity);
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
        if (postDTO.getTitle() != null && !postDTO.getTitle().isEmpty() && !postDTO.getTitle().equals(existingPost.getTitle())) {
            existingPost.setTitle(postDTO.getTitle());
            existingPost.setSlug(postUtilityService.generateSlug(postDTO.getTitle())); // Cập nhật slug nếu tiêu đề thay đổi
        }

        // Logic 1: Cập nhật trạng thái của hình ảnh cũ và mới dựa trên publicId
        if (postDTO.getPublicId() != null && !postDTO.getPublicId().isEmpty()) {
            if (!postDTO.getPublicId().equals(existingPost.getPublicId())) {
                // Giảm usageCount của hình ảnh cũ
                updateImageUsageByPublicId(existingPost.getPublicId(), false, true);

                // Tăng usageCount của hình ảnh mới
                updateImageUsageByPublicId(postDTO.getPublicId(), true, true);

                // Cập nhật thumbnail và publicId cho bài viết
                existingPost.setThumbnail(postDTO.getThumbnail());
                existingPost.setPublicId(postDTO.getPublicId());
            }
        }

        // Logic 2: Cập nhật trạng thái của các hình ảnh trong nội dung bài viết
        if (postDTO.getContent() != null && !postDTO.getContent().isEmpty()) {
            // Lấy danh sách publicId từ nội dung hiện tại
            List<String> currentPublicIds = extractPublicIdsFromImageUrls(existingPost.getContent());
            // Lấy danh sách publicId từ nội dung mới
            List<String> newPublicIds = extractPublicIdsFromImageUrls(postDTO.getContent());

            // Giảm usageCount và cập nhật isUsed cho các hình ảnh không còn trong nội dung mới
            for (String publicId : currentPublicIds) {
                if (!newPublicIds.contains(publicId)) {
                    updateImageUsageByPublicId(publicId, false, false);
                }
            }

            // Tăng usageCount và cập nhật isUsed cho các hình ảnh mới
            for (String publicId : newPublicIds) {
                if (!currentPublicIds.contains(publicId)) {
                    updateImageUsageByPublicId(publicId, true, false);
                }
            }

            // Cập nhật nội dung và excerpt của bài viết
            existingPost.setContent(postDTO.getContent());
            existingPost.setExcerpt(postUtilityService.generateExcerpt(postDTO.getContent())); // Cập nhật excerpt với độ dài 200 ký tự
        }


        if (postDTO.getStatus() != null) {
            existingPost.setStatus(postDTO.getStatus());
        }

        // Kiểm tra và cập nhật giá trị priority
        if (Boolean.TRUE.equals(postDTO.getPriority())) {
            int maxPriority = postRepository.findMaxPriority();
            existingPost.setPriority(maxPriority + 1);
        }

        // Cập nhật thông tin Meta nếu có thay đổi
        MetaEntity meta = existingPost.getMeta();
        if (meta != null) {
            updateMetaEntity(meta, postDTO);
        }

        // Xử lý các thẻ (tags)
        if (postDTO.getTags() != null) {
            Set<TagEntity> tags = processTags(postDTO.getTags());
            existingPost.setTags(tags);
        }

        existingPost.setRevisionCount(existingPost.getRevisionCount() + 1);
        // Lưu bài viết đã cập nhật vào cơ sở dữ liệu
        PostEntity updatedPost = postRepository.save(existingPost);
        // Trả về đối tượng PostResponse
        return PostResponse.fromPost(updatedPost);
    }

    private void updateImageUsageByPublicId(String publicId, boolean isUsed, boolean throwErrorIfNotFound) {
        if (publicId != null && !publicId.isEmpty()) {
            Optional<ImageEntity> optionalImageEntity = imageRepository.findByPublicId(publicId);
            if (optionalImageEntity.isPresent()) {
                ImageEntity imageEntity = optionalImageEntity.get();
                imageEntity.setIsUsed(isUsed);
                imageEntity.setUsageCount(imageEntity.getUsageCount() + (isUsed ? 1 : -1));
                imageRepository.save(imageEntity);
            } else if (throwErrorIfNotFound) {
                throw new RuntimeException("Image not found for URL: " + publicId);
            } else {
                System.err.println("Image not found for URL: " + publicId);
            }
        }
    }
    public List<String> extractPublicIdsFromImageUrls(String content) {
        Set<String> imageUrls = extractImageUrls(content);
        List<String> publicIds = new ArrayList<>();

        if (imageUrls.isEmpty()) {
            return publicIds; // Trả về danh sách rỗng nếu không có hình ảnh
        }

        for (String imageUrl : imageUrls) {
            try {
                String publicId = extractPublicId(imageUrl);
                publicIds.add(publicId);
            } catch (IllegalArgumentException e) {
                // Bỏ qua các URL không hợp lệ
                System.err.println("Invalid image URL: " + imageUrl);
            }
        }

        return publicIds;
    }
    private Set<String> extractImageUrls(String content) {
        // Kiểm tra null hoặc chuỗi rỗng
        if (content == null || content.trim().isEmpty()) {
            return Collections.emptySet();
        }

        // Tạo một tập hợp để lưu trữ các URL hình ảnh
        Set<String> imageUrls = new HashSet<>();

        // Phân tích nội dung HTML
        Document document = Jsoup.parse(content);

        // Lấy tất cả các thẻ <img>
        Elements imgElements = document.select("img");

        // Duyệt qua các thẻ <img> để lấy giá trị thuộc tính "src"
        imgElements.stream()
                .map(img -> img.attr("src"))
                .filter(src -> src != null && !src.isEmpty()) // Lọc ra các giá trị hợp lệ
                .forEach(imageUrls::add);

        return imageUrls;
    }

    public String extractPublicId(String url) {
        // Tìm vị trí của phần "/upload/" trong URL
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1) {
            throw new IllegalArgumentException("Invalid Cloudinary URL");
        }

        // Lấy phần còn lại của URL sau "/upload/"
        String remainingUrl = url.substring(uploadIndex + 8);

        // Kiểm tra và bỏ qua phần phiên bản nếu có
        if (remainingUrl.startsWith("v")) {
            int slashIndex = remainingUrl.indexOf('/');
            if (slashIndex != -1) {
                remainingUrl = remainingUrl.substring(slashIndex + 1);
            } else {
                throw new IllegalArgumentException("Invalid Cloudinary URL");
            }
        }

        // Tìm vị trí của dấu chấm cuối cùng (trước phần định dạng)
        int dotIndex = remainingUrl.lastIndexOf('.');
        if (dotIndex == -1) {
            throw new IllegalArgumentException("Invalid Cloudinary URL");
        }

        // Trích xuất `public_id` từ phần còn lại của URL
        return remainingUrl.substring(0, dotIndex);
    }
    private void updateMetaEntity(MetaEntity meta, UpdatePostDTO postDTO) {
        boolean titleChanged = false;

        if (postDTO.getTitle() != null && !postDTO.getTitle().isEmpty()) {
            meta.setMetaTitle(postDTO.getTitle());
            meta.setOgTitle(postDTO.getTitle());
            titleChanged = true;
        }

        if (postDTO.getContent() != null && !postDTO.getContent().isEmpty()) {
            meta.setMetaDescription(postUtilityService.generateMetaDescription(postDTO.getContent()));
            meta.setOgDescription(postUtilityService.generateOgDescription(postDTO.getContent()));
        }

        if (postDTO.getThumbnail() != null && !postDTO.getThumbnail().isEmpty()) {
            meta.setOgImage(postDTO.getThumbnail());
        }

        if (titleChanged) {
            String slug = baseUrl + "/" + postUtilityService.generateSlug(postDTO.getTitle());
            meta.setSlug(slug);
        }
    }

    @Override
    @Transactional
    public void disablePost(long id) throws DataNotFoundException {
        PostEntity post = findPostByIdOrThrow(id);
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(long id) throws DataNotFoundException {
        PostEntity post = findPostByIdOrThrow(id);
        postRepository.delete(post);
    }


    @Override
    @Transactional
    public void deletePosts(List<Long> ids) {
        List<PostEntity> posts = postRepository.findAllById(ids);
        posts.forEach(post -> post.setStatus(PostStatus.DELETED));
        postRepository.saveAll(posts);
    }


    @Override
    public boolean existsPostByTitle(String title) {
        return postRepository.existsByTitle(title);
    }

    @Override
    public PostResponse getPostBySlug( String slug) throws DataNotFoundException {
        // Lấy thông tin bài viết hiện có bằng slug, status và visibility
        PostEntity post = postRepository.findPostBySlugAndStatusAndVisibility(slug, PostStatus.PUBLISHED, PostVisibility.PUBLIC)
                .orElseThrow(() -> new DataNotFoundException("Cannot find post with slug " + slug));
        // Tăng giá trị viewCount lên 1
        post.incrementViewCount();
        // Lưu  thay đổi vào cơ sở dữ liệu
        postRepository.save(post);
        // Trả về đối tượng PostResponse
        return PostResponse.fromPost(post);
    }

    @Override
    public Page<PostResponse> getRecentPosts(Pageable Pageable) {
        Page<PostEntity> postsPage = postRepository.findRecentPosts(PostStatus.PUBLISHED, PostVisibility.PUBLIC, Pageable);
        return postsPage.map(PostResponse::fromPost);
    }



    @Override
    public Page<PostResponse> searchPosts(String keyword, String categorySlug, String tagSlug,  Pageable pageable) {
        Page<PostEntity> postEntities = postRepository.searchPostsForUser(
                keyword, categorySlug, tagSlug ,PostStatus.PUBLISHED, PostVisibility.PUBLIC,pageable);
        return postEntities.map(PostResponse::fromPost);
    }


    // Phương thức để phân tích nội dung bài viết và tìm các URL hình ảnh
    private Set<TagEntity> processTags(Set<TagDTO> tagDTOs) {
        Set<TagEntity> tags = new HashSet<>();
        for (TagDTO tagDTO : tagDTOs) {
            String slug = postUtilityService.generateSlug(tagDTO.getName());
            TagEntity tag = tagRepository.findByName(tagDTO.getName())
                    .orElseGet(() -> tagRepository.save(new TagEntity(tagDTO.getName(), slug)));
            tags.add(tag);
        }
        return tags;
    }
    private MetaEntity createMetaEntity(PostDTO postDTO) {
        String slug = baseUrl + "/" + postDTO.getTitle().replaceAll("\\s+", "-").toLowerCase();
        return MetaEntity.builder()
                .metaTitle(postDTO.getTitle())
                .metaDescription(postUtilityService.generateMetaDescription(postDTO.getContent()))
                .ogTitle(postDTO.getTitle())
                .ogDescription(postUtilityService.generateOgDescription(postDTO.getContent()))
                .ogImage(postDTO.getThumbnail())
                .viewport("width=device-width, initial-scale=1.0")
                .robots("index, follow")
                .slug(slug)
                .build();
    }

    private PostEntity findPostByIdOrThrow(long id) throws DataNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find post with id " + id));
    }

}
