package com.pivinadanang.blog.dtos;


import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "content is required")
    @Size(max = 65535, message = "Content must be less than or equal to 65535 characters")
    private String content;

    @JsonProperty("category_id")
    private Long categoryId;

    @Size(max = 2048, message = "Thumbnail URL must be less than or equal to 2048 characters")
    private String thumbnail;

    @JsonProperty("public_id")
    private String publicId;

    private PostStatus status;

    private PostVisibility visibility;
    @NotEmpty(message = "Tags cannot be empty")
    @Size(max = 5, message = "Tags cannot be more than 5")
    private Set<@Valid TagDTO> tags = new HashSet<>();


}
