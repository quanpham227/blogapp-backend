package com.pivinadanang.blog.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.enums.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePostDTO {
    private String title;

    private String content;

    @JsonProperty("category_id")
    private Long categoryId;

    private String thumbnail;

    private PostStatus status;

}
