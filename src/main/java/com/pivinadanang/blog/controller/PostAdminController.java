package com.pivinadanang.blog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.components.SecurityUtils;
import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.UpdatePostDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.post.PostListResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.post.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/posts")
@Validated
@RequiredArgsConstructor
public class PostAdminController {
    private static final Logger logger = LoggerFactory.getLogger(PostAdminController.class);
    private final IPostService postService;
    private final LocalizationUtils localizationUtils;
    private final SecurityUtils securityUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> createPost(@Valid @RequestBody PostDTO postDTO) throws Exception {
        if (postService.existsPostByTitle(postDTO.getTitle())) {
            return buildBadRequestResponse("Post title already exists");
        }
        if (postDTO.getThumbnail() == null || postDTO.getThumbnail().isEmpty() || postDTO.getPublicId() == null || postDTO.getPublicId().isEmpty()) {
            return buildBadRequestResponse("Thumbnail or publicId is required");
        }

        PostResponse postResponse = postService.createPost(postDTO);
        return buildCreatedResponse(postResponse, "Insert post successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getPostById(@PathVariable Long id) throws Exception {
        PostResponse postResponse = postService.getPostById(id);
        return buildOkResponse(postResponse, "Get post successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updatePost(@Valid @RequestBody UpdatePostDTO updatePostDTO, @PathVariable Long id) throws Exception {
        if (updatePostDTO == null || updatePostDTO.getTitle() == null || updatePostDTO.getTitle().isEmpty()) {
            return buildBadRequestResponse("Invalid UpdatePostDTO");
        }

        PostResponse updatedPost = postService.updatePost(id, updatePostDTO);
        return buildOkResponse(updatedPost, "Update post successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteOrDisablePost(@PathVariable Long id, @RequestParam boolean isPermanent) throws Exception {
        UserEntity loggedInUser = securityUtils.getLoggedInUser();
        if (loggedInUser == null) {
            return buildForbiddenResponse("You must be logged in to perform this action.");
        }

        if (securityUtils.hasRole("ROLE_ADMIN") && isPermanent) {
            return buildForbiddenResponse("Admins are not allowed to permanently delete posts.");
        }

        if (!securityUtils.hasRole("ROLE_MODERATOR") && !securityUtils.hasRole("ROLE_ADMIN")) {
            return buildForbiddenResponse("You do not have permission to perform this action.");
        }

        if (isPermanent) {
            postService.deletePost(id);
        } else {
            postService.disablePost(id);
        }
        return buildOkResponse(null, isPermanent ? "Delete post successfully" : "Disable post successfully");
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getPosts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws JsonProcessingException {
        if (page < 0) {
            return buildBadRequestResponse("Page index must not be less than zero");
        }
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<PostResponse> postPage = postService.getAllPosts(keyword, categoryId, status, startDate, endDate, pageRequest);
        List<PostResponse> postResponses = postPage.getContent();
        int totalPages = postPage.getTotalPages();
        postResponses.forEach(post -> post.setTotalPages(totalPages));
        PostListResponse postListResponse = PostListResponse.builder()
                .posts(postResponses)
                .totalPages(totalPages)
                .build();
        return buildOkResponse(postListResponse, "Get posts successfully");
    }

    private ResponseEntity<ResponseObject> buildBadRequestResponse(String message) {
        return ResponseEntity.badRequest()
                .body(ResponseObject.builder()
                        .message(message)  // Truyền trực tiếp thông điệp
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
    }

    private ResponseEntity<ResponseObject> buildForbiddenResponse(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseObject.builder()
                        .message(message)  // Truyền trực tiếp thông điệp
                        .status(HttpStatus.FORBIDDEN)
                        .build());
    }

    private ResponseEntity<ResponseObject> buildOkResponse(Object data, String message) {
        return ResponseEntity.ok(ResponseObject.builder()
                .message(message)  // Truyền trực tiếp thông điệp
                .status(HttpStatus.OK)
                .data(data)
                .build());
    }

    private ResponseEntity<ResponseObject> buildCreatedResponse(Object data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .data(data)
                        .message(message)  // Truyền trực tiếp thông điệp
                        .build());
    }

    private ResponseEntity<ResponseObject> buildErrorResponse(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }

    private ResponseEntity<ResponseObject> handleException(RuntimeException e) {
        if (e.getMessage().equals("Post not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
        }
        return buildErrorResponse(e);
    }
}