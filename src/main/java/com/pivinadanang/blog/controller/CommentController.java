package com.pivinadanang.blog.controller;
import com.pivinadanang.blog.components.SecurityUtils;
import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.dtos.UpdateCommentDTO;
import com.pivinadanang.blog.enums.CommentStatus;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.comment.CommentListResponse;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import com.pivinadanang.blog.services.comment.CommentService;
import com.pivinadanang.blog.services.comment.ICommentService;
import com.pivinadanang.blog.services.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/comments")

@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;
    private final SecurityUtils securityUtils;
    private final IUserService userService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllComment(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(required = false) CommentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws Exception {
        Pageable pageable = PageRequest.of(
                page, limit,
                Sort.by("createdAt").descending()
        );
        Page<CommentResponse> commentResponsePage = commentService.findAll(keyword, status, pageable);

        // Nếu không có comment, trả về danh sách rỗng
        List<CommentResponse> commentResponses = commentResponsePage != null ? commentResponsePage.getContent() : new ArrayList<>();
        int totalPages = commentResponsePage != null ? commentResponsePage.getTotalPages() : 0;

        CommentListResponse commentListResponse = new CommentListResponse();
        commentListResponse.setComments(commentResponses);
        commentListResponse.setTotalPages(totalPages);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get comments list successfully")
                .status(HttpStatus.OK)
                .data(commentListResponse)
                .build());
    }


    @GetMapping("/post/{id}")
    public ResponseEntity<ResponseObject> getCommentsByPostId(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CommentResponse> commentResponses = commentService.getCommentsByPostId(id, pageable);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get comments successfully")
                .status(HttpStatus.OK)
                .data(commentResponses)
                .build());
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> addComment(@Valid @RequestBody CommentDTO commentDTO) {
        UserEntity loginUser = securityUtils.getLoggedInUser();
        if(!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot comment as another user",
                            HttpStatus.BAD_REQUEST,
                            null));
        }
        CommentResponse comment  = commentService.insertComment(commentDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Insert comment successfully")
                        .status(HttpStatus.OK)
                        .data(comment)
                        .build());
    }
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateComment(@PathVariable("id") Long commentId, @Valid @RequestBody UpdateCommentDTO updateCommentDTO) throws Exception {
        UserEntity loginUser = securityUtils.getLoggedInUser();
        String loginUserRole = loginUser.getRole().getName();

        // Check if the user has permission to update the comment
        if (loginUserRole.equals(RoleEntity.USER) && !Objects.equals(loginUser.getId(), updateCommentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot update another user's comment",
                            HttpStatus.BAD_REQUEST,
                            null));
        }

        if (loginUserRole.equals(RoleEntity.ADMIN) && !Objects.equals(loginUser.getId(), updateCommentDTO.getUserId())) {
            UserEntity commentOwner = userService.getUserById(updateCommentDTO.getUserId());
            String commentOwnerRole = commentOwner.getRole().getName();
            if (commentOwnerRole.equals(RoleEntity.ADMIN) || commentOwnerRole.equals(RoleEntity.MODERATOR)) {
                return ResponseEntity.badRequest().body(
                        new ResponseObject(
                                "Admin cannot update another admin's or moderator's comment",
                                HttpStatus.BAD_REQUEST,
                                null));
            }
        }

        // Moderators can update any comment, so no additional checks are needed

        CommentResponse commentResponse = commentService.updateComment(commentId, updateCommentDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("update comment successfully")
                .status(HttpStatus.OK)
                .data(commentResponse)
                .build());
    }

    @PostMapping("/generateFakeComments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> generateFakeComments() throws Exception {
        commentService.generateFakeComments();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert fake comments succcessfully")
                .data(null)
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("/reply")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> replyComment(@Valid @RequestBody CommentDTO commentDTO) {
        UserEntity loginUser = securityUtils.getLoggedInUser();
        if (!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot reply as another user",
                            HttpStatus.BAD_REQUEST,
                            null));
        }

        CommentResponse comment = commentService.replyComment(commentDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Reply comment successfully")
                        .status(HttpStatus.OK)
                        .data(comment)
                        .build());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteComment(@PathVariable("id") Long commentId) throws Exception {
        UserEntity loginUser = securityUtils.getLoggedInUser();
        CommentResponse commentResponse = commentService.getCommentById(commentId);
        if (commentResponse == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                            .message("Comment not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .build());
        }
        if (loginUser.getRole().getName().equals(RoleEntity.ADMIN) && commentResponse.getUserRole().equals(RoleEntity.ADMIN) && !Objects.equals(loginUser.getId(), commentResponse.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Admin không thể xoá bình luận của admin khác")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        if (!loginUser.getRole().getName().equals(RoleEntity.ADMIN) && !Objects.equals(loginUser.getId(), commentResponse.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Bạn không thể xoá bình luận của người khác")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Delete comment successfully")
                        .status(HttpStatus.OK)
                        .data(null)
                        .build());
    }

    @PutMapping("/updateStatus/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateCommentStatus(
            @PathVariable("id") Long commentId,
            @RequestBody Map<String, CommentStatus> request) throws Exception {
        CommentStatus status = request.get("status");
        UserEntity loginUser = securityUtils.getLoggedInUser();
        CommentResponse commentResponse = commentService.getCommentById(commentId);
        if (commentResponse == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Comment not found")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }

        // Check if the user has permission to update the comment status
        if (!loginUser.getRole().getName().equals(RoleEntity.ADMIN) &&
                !loginUser.getRole().getName().equals(RoleEntity.MODERATOR) &&
                !Objects.equals(loginUser.getId(), commentResponse.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("You do not have permission")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        // Update the comment status
        commentService.updateCommentStatus(commentId, status);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Update comment status successfully")
                        .status(HttpStatus.OK)
                        .data(null)
                        .build());
    }
}
