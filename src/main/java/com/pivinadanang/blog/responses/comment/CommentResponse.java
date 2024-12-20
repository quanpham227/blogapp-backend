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
                .userId(comment.getUser() != null ? comment.getUser().getId() : null) // Kiểm tra không null cho userId
                .fullName(comment.getUser() != null ? comment.getUser().getFullName() : null) // Kiểm tra không null cho fullName
                .email(comment.getUser() != null ? comment.getUser().getEmail() : null) // Kiểm tra không null cho email
                .profileImage(comment.getUser() != null ? comment.getUser().getProfileImage() : null) // Kiểm tra không null cho profileImage
                .status(comment.getStatus() != null ? comment.getStatus().name() : "UNKNOWN") // Xử lý null cho status
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null) // Xử lý null cho parentCommentId
                .postId(comment.getPost() != null ? comment.getPost().getId() : null) // Kiểm tra không null cho postId
                .replies(replies)
                .userRole(comment.getUser() != null && comment.getUser().getRole() != null ? comment.getUser().getRole().getName() : null) // Kiểm tra không null cho userRole
                .build();
        result.setCreatedAt(comment.getCreatedAt());
        result.setUpdatedAt(comment.getUpdatedAt());
        return result;
    }
}
