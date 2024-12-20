package com.pivinadanang.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.post.PostListResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.post.IPostService;
import com.pivinadanang.blog.services.post.PostRedisService;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/user/posts")
@Validated
@RequiredArgsConstructor
public class PostUserController {
    private  static final Logger logger = LoggerFactory.getLogger(PostUserController.class);
    private final IPostService postService;
    private final LocalizationUtils localizationUtils;
    private final PostRedisService postRedisService;


    private Pageable createPageable(int page, int limit) {
        return PageRequest.of(page, limit);
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> searchPosts(
            @RequestParam(defaultValue = "",required = false ) String keyword,
            @RequestParam (defaultValue = "",required = false) String categorySlug,
            @RequestParam (defaultValue = "",required = false) String tagSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int limit) throws JsonProcessingException {
        Pageable pageable = createPageable(page, limit);

        List<PostResponse> postResponses = postRedisService.getAllPosts(keyword, categorySlug, tagSlug, pageable);

        if (postResponses == null || postResponses.isEmpty()) {
            Page<PostResponse> postPage = postService.searchPosts(keyword,categorySlug, tagSlug, pageable);
            postResponses = postPage.getContent();
            int totalPages = postPage.getTotalPages();
            postResponses.forEach(post -> post.setTotalPages(totalPages));
            postRedisService.saveAllPosts(postResponses, keyword, categorySlug, tagSlug, pageable);
        }

        PostListResponse postListResponse = PostListResponse.builder()
                .posts(postResponses)
                .totalPages(postResponses.isEmpty() ? 0 : postResponses.get(0).getTotalPages())
                .build();



        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get  posts successfully")
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
        Pageable pageable = createPageable(page, limit);
        // Kiểm tra cache Redis
        List<PostResponse> postResponses = postRedisService.getRecentPosts(pageable);

        if (postResponses != null && !postResponses.isEmpty()) {
            totalPages = postResponses.get(0).getTotalPages();
        } else {
            // Nếu cache trống, lấy dữ liệu từ database
            Page<PostResponse> postPage = postService.getRecentPosts(pageable);
            totalPages = postPage.getTotalPages();
            postResponses = postPage.getContent();
            // Bổ sung totalPages vào các đối tượng PostResponse
            for (PostResponse post : postResponses) {
                post.setTotalPages(totalPages);
            }
            // Lưu kết quả vào Redis
            postRedisService.saveRecentPosts(postResponses, pageable);
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
        PostResponse postResponse = postRedisService.getPostBySlug(slug);

        if (postResponse == null || postResponse.getSlug() == null) {
            postResponse = postService.getPostBySlug(slug);
            postRedisService.savePostBySlug(postResponse, slug);
        }

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(postResponse)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY))
                .build());
    }
}



