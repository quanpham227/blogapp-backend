package com.pivinadanang.blog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlideDTO {
    @NotEmpty(message = "name cannot be empty")
    private String title;
    @JsonProperty("image_url")
    private String imageUrl;
    private String description;
    private Boolean status = true;
    private Integer order ;
    @JsonProperty("public_id")
    private String publicId;
    private String link;
}
