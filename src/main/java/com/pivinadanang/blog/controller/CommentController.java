package com.pivinadanang.blog.controller;
import com.github.javafaker.Faker;
import com.pivinadanang.blog.components.SecurityUtils;
import com.pivinadanang.blog.dtos.CommentDTO;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import com.pivinadanang.blog.services.comment.CommentService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/comments")

@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final SecurityUtils securityUtils;


    @GetMapping("/post/{id}")
    public ResponseEntity<ResponseObject> getCommentsByPostId(@PathVariable Long id) {
        List<CommentResponse> commentResponses = commentService.getCommentsByPostId(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get comments successfully")
                .status(HttpStatus.OK)
                .data(commentResponses)
                .build());
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> updateComment(@PathVariable("id") Long commentId, @Valid @RequestBody CommentDTO commentDTO) throws Exception {
        UserEntity loginUser = securityUtils.getLoggedInUser();
        if (!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot update another user's comment",
                            HttpStatus.BAD_REQUEST,
                            null));

        }
        CommentResponse commentResponse =  commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                        .message("update comment successfully")
                        .status(HttpStatus.OK)
                        .data(commentResponse)
                        .build());
    }

    @PostMapping("/generateFakeComments")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> generateFakeComments() throws Exception {
        commentService.generateFakeComments();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert fake comments succcessfully")
                .data(null)
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("/reply")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
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
}
