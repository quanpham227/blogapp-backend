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
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "content is required")
    private String content;

    @JsonProperty("category_id")
    private Long categoryId;

    private String thumbnail;

    @JsonProperty("public_id")
    private String publicId;

    private PostStatus status;

    private PostVisibility visibility;
    @NotEmpty(message = "Tags cannot be empty")
    @Size(max = 5, message = "Tags cannot be more than 5")
    private Set<@Valid TagDTO> tags = new HashSet<>();


}
