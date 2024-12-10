package com.pivinadanang.blog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDTO {

    @JsonProperty("user_id")
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @JsonProperty("content")
    @NotEmpty(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content must be less than or equal to 1000 characters")
    private String content;
}
