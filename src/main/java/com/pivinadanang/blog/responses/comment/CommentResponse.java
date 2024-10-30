package com.pivinadanang.blog.responses.comment;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import com.pivinadanang.blog.responses.user.UserResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse extends BaseResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    private String status;

    @JsonProperty("parent_comment_id")
    private Long parentCommentId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("replies")
    private List<CommentResponse> replies;

    @JsonProperty("user_role") // Thêm thuộc tính userRole
    private String userRole;

    private static final int MAX_DEPTH = 1;

    public static CommentResponse fromComment(CommentEntity comment) {
        return fromComment(comment, 0);
    }

    private static CommentResponse fromComment(CommentEntity comment, int depth) {
        if (depth > MAX_DEPTH) {
            return null;
        }

        List<CommentResponse> replies = new ArrayList<>();
        if (depth < MAX_DEPTH && comment.getReplies() != null) {
            replies = comment.getReplies().stream()
                    .map(reply -> fromComment(reply, depth + 1))
                    .filter(Objects::nonNull) // Loại bỏ các phần tử null
                    .collect(Collectors.toList());
        }
        CommentResponse result = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .fullName(comment.getUser().getFullName())
                .email(comment.getUser().getEmail())
                .profileImage(comment.getUser().getProfileImage())
                .status(comment.getStatus().name())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .postId(comment.getPost().getId())
                .replies(replies)
                .userRole(comment.getUser().getRole().getName())
                .build();
        result.setCreatedAt(comment.getCreatedAt());
        result.setUpdatedAt(comment.getUpdatedAt());
        return result;
    }
}
