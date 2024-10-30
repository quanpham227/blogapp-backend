package com.pivinadanang.blog.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.pivinadanang.blog.enums.CommentStatus;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @JsonProperty("post_id")
    @NotNull(message = "Post ID cannot be null")
    private Long postId;

    @JsonProperty("user_id")
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @JsonProperty("content")
    @NotEmpty(message = "Content cannot be empty")
    private String content;

    @JsonProperty("parent_comment_id")
    private Long parentCommentId;
}
