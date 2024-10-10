package com.pivinadanang.blog.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePostDTO {
    private Long id;

    private String title;

    private String content;

    @JsonProperty("category_id")
    private Long categoryId;

    private String thumbnail;

    @JsonProperty("public_id")
    private String publicId;

    private PostStatus status;

    private PostVisibility visibility;

    @Size(max = 5, message = "Tags cannot be more than 5")
    private Set<TagDTO> tags;

    @JsonProperty("view_count")
    private Integer viewCount;

    @JsonProperty("revision_count")
    private Integer revisionCount;

    @JsonProperty("ratings_count")
    private Integer ratingsCount;

    @JsonProperty("comment_count")
    private Integer commentCount;


}
