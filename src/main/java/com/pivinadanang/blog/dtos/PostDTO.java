package com.pivinadanang.blog.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.ultils.SlugUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "content is required")
    private String content;

    private Long categoryId;

    private String thumbnail;

    private PostStatus status;

}
