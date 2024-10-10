package com.pivinadanang.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.post.PostListResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.post.IPostService;
import com.pivinadanang.blog.services.post.PostRedisService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/posts")
@Validated
@RequiredArgsConstructor
public class PostController {
    private  static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final IPostService postService;
    private final LocalizationUtils localizationUtils;
    private final PostRedisService postRedisService;
    private final PostRepository postRepository;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createPost(@Valid @RequestBody PostDTO postDTO, BindingResult result) throws Exception{
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Validation errors")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errorMessages)
                            .build());
        }
        if(postService.existsPostByTitle(postDTO.getTitle())){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        if(postDTO.getThumbnail() == null || postDTO.getThumbnail().isEmpty()){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_THUMBNAIL_REQUIRED))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        PostResponse postResponse = postService.createPost(postDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(postResponse)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_SUCCESSFULLY))
                .build());
    }
    @GetMapping("/details/{id}")
    public ResponseEntity<ResponseObject> getPostById(@PathVariable Long id) throws Exception {
        PostResponse existingPost = postService.getPostById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(existingPost)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY))
                .build());
    }
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updatePost(@Valid @RequestBody UpdatePostDTO postDTO, @PathVariable Long id, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Validation errors")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errorMessages)
                            .build());
        }
        PostResponse postResponse = postService.updatePost(id,postDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY))
                        .status(HttpStatus.CREATED)
                        .data(postResponse)
                        .build());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deletePost(@PathVariable Long id) throws Exception{
            postService.deletePost(id);
            return ResponseEntity.ok(ResponseObject.builder()
                            .data(null)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_POST_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                    .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getPosts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false, name = "created_at") @DateTimeFormat(pattern = "yyyy-MM") YearMonth createdAt)
            throws JsonProcessingException {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
//        List<PostResponse> postResponses = postRedisService.getAllPosts(keyword, categoryId, status, createdAt, pageRequest);
//
//        if (postResponses == null || postResponses.isEmpty()) {
//            Page<PostResponse> postPage = postService.getAllPosts(keyword, categoryId, status, createdAt, pageRequest);
//            postResponses = postPage.getContent();
//            int totalPages = postPage.getTotalPages();
//            postResponses.forEach(post -> post.setTotalPages(totalPages));
//            postRedisService.saveAllPosts(postResponses, keyword, categoryId, status, createdAt, pageRequest);
//        }
//        PostListResponse postListResponse = PostListResponse.builder()
//                .posts(postResponses)
//                .totalPages(postResponses.isEmpty() ? 0 : postResponses.get(0).getTotalPages())
//                .build();


        Page<PostResponse> postPage = postService.getAllPosts(keyword, categoryId, status, createdAt, pageRequest);
        List<PostResponse> postResponses = postPage.getContent();
        int totalPages = postPage.getTotalPages();
        postResponses.forEach(post -> post.setTotalPages(totalPages));
        PostListResponse postListResponse = PostListResponse.builder()
                .posts(postResponses)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get posts successfully")
                .status(HttpStatus.OK)
                .data(postListResponse)
                .build());

    }

    @GetMapping("/recent")
    public ResponseEntity<ResponseObject> getRecentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit) throws JsonProcessingException {
        int totalPages = 0;

        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        // Kiểm tra cache Redis
        List<PostResponse> postResponses = postRedisService.getRecentPosts(pageRequest);

        if (postResponses != null && !postResponses.isEmpty()) {
            totalPages = postResponses.get(0).getTotalPages();
        } else {
            // Nếu cache trống, lấy dữ liệu từ database
            Page<PostResponse> postPage = postService.getRecentPosts(pageRequest);
            totalPages = postPage.getTotalPages();
            postResponses = postPage.getContent();

            // Bổ sung totalPages vào các đối tượng PostResponse
            for (PostResponse post : postResponses) {
                post.setTotalPages(totalPages);
            }

            // Lưu kết quả vào Redis
            postRedisService.saveRecentPosts(postResponses, pageRequest);
        }

        PostListResponse postListResponse = PostListResponse.builder()
                .posts(postResponses)
                .totalPages(totalPages)
                .build();

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get recent posts successfully")
                .status(HttpStatus.OK)
                .data(postListResponse)
                .build());
    }


    @GetMapping("/{slug}")
    public ResponseEntity<ResponseObject> getPostBySlug(@PathVariable String slug) throws Exception {
        PostEntity postEntity = postService.getPostBySlug(slug);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(PostResponse.fromPost(postEntity))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY))
                .build());
    }


    @PostMapping("/generateFakePosts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateFakePosts(){
        Faker faker = new Faker(new Locale("vi"));
        for(int i = 0; i < 1000; i++){
            String title = faker.book().title() + " - " + faker.book().title() + " - " + faker.book().title();
            if(postService.existsPostByTitle(title)){
                continue;
            }
            String content = faker.lorem().paragraphs(3).toString();
            Long categoryId = (long) faker.number().numberBetween(1,5);
            String[] imageUrls = {
                   "https://drive.google.com/thumbnail?id=1Xj4C6eKfVL1krZ6ylNvhXFIp8X2Xzx5e&sz=w4000",
                    "https://drive.google.com/thumbnail?id=15He3o1pAAVGujxlHBc7ef2YK-4moa9if&sz=w4000",
                    "https://drive.google.com/thumbnail?id=139rjuisL9A1XP0blu1hXgkmyfqDh5suE&sz=w4000",
                    "https://drive.google.com/thumbnail?id=1PtPUaw92MJF1Ph_zBYgeoW3nCKo_JKjP&sz=w4000",
                    "https://drive.google.com/thumbnail?id=1KMxf6wpWO4ry_DtWkWvs-IgimycOmDQv&sz=w4000"
            };
            String imageUrl = imageUrls[faker.random().nextInt(imageUrls.length)];
            PostDTO postDTO = PostDTO.builder()
                    .title(title)
                    .content(content)
                    .categoryId(categoryId)
                    .build();
            try {
                postService.createPost(postDTO);
            }catch (Exception exception){
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }
        return ResponseEntity.ok("Posts generated successfully");
    }


    @GetMapping("/month-years")
    public ResponseEntity<ResponseObject> getAllMonthYears () {
        List<String> monthYears = postService.getAllMonthYears();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get all month years successfully")
                .status(HttpStatus.OK)
                .data(monthYears)
                .build());
    }



    @GetMapping("/counts")
    public ResponseEntity<ResponseObject> getPostStatusCounts() {
        try {
            Map<PostStatus, Long> postCounts = postService.getPostCountsByStatus();

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(postCounts)
                    .message("Successfully retrieved post counts.")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error retrieving post counts: " + e.getMessage())
                            .build());
        }
    }


}
