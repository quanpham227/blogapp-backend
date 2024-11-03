package com.pivinadanang.blog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlideDTO {
    @NotEmpty(message = "Title cannot be empty")
    @Size(max = 50, message = "Title must be less than or equal to 50 characters")
    private String title;

    @JsonProperty("image_url")
    @Size(max = 2048, message = "Image URL must be less than or equal to 2048 characters")
    private String imageUrl;

    @Size(max = 1000, message = "Description must be less than or equal to 1000 characters")
    private String description;

    private Boolean status = true;

    private Integer order;

    @JsonProperty("public_id")
    @Size(max = 255, message = "Public ID must be less than or equal to 255 characters")
    private String publicId;

    @Size(max = 2048, message = "Link must be less than or equal to 2048 characters")
    private String link;
}
