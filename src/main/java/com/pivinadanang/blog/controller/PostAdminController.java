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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/admin/posts")
@Validated
@RequiredArgsConstructor
public class PostAdminController {
    private  static final Logger logger = LoggerFactory.getLogger(PostAdminController.class);
    private final IPostService postService;
    private final LocalizationUtils localizationUtils;
    private final SecurityUtils securityUtils;


    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> createPost(@Valid @RequestBody PostDTO postDTO) throws Exception{
        if(postService.existsPostByTitle(postDTO.getTitle())){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        if(postDTO.getThumbnail() == null || postDTO.getThumbnail().isEmpty() || postDTO.getPublicId() == null || postDTO.getPublicId().isEmpty()){
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updatePost(@Valid @RequestBody UpdatePostDTO postDTO, @PathVariable Long id) throws Exception {
        PostResponse postResponse = postService.updatePost(id,postDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .data(postResponse)
                        .build());
    }
    @DeleteMapping("/disable/{id}/{isPermanent}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteOrDisablePost(@PathVariable Long id, @PathVariable boolean isPermanent) throws Exception {
        UserEntity loggedInUser = securityUtils.getLoggedInUser();
        if (loggedInUser == null) {
            throw new AccessDeniedException("You must be logged in to perform this action.");
        }

        if (securityUtils.hasRole("ROLE_MODERATOR")) {
            if (isPermanent) {
                postService.deletePost(id);
            } else {
                postService.disablePost(id);
            }
        } else if (securityUtils.hasRole("ROLE_ADMIN")) {
            if (isPermanent) {
                throw new AccessDeniedException("Admins are not allowed to permanently delete posts.");
            } else {
                postService.disablePost(id);
            }
        } else {
            throw new AccessDeniedException("You do not have permission to perform this action.");
        }

        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message(localizationUtils.getLocalizedMessage(isPermanent ? MessageKeys.DELETE_POST_SUCCESSFULLY : MessageKeys.DISABLE_POST_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("")
    public ResponseEntity<ResponseObject> getPosts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate)
            throws JsonProcessingException {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<PostResponse> postPage = postService.getAllPosts(keyword, categoryId, status, startDate, endDate, pageRequest);
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

}
