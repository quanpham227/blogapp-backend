package com.pivinadanang.blog.responses.slide;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.SlideEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlideResponse extends BaseResponse {
    private Long id;
    private String title;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("public_id")
    private String publicId;
    private String link;
    private String description;
    private Boolean status = true;
    private Integer order = 0;

    public static SlideResponse fromSlide (SlideEntity slide){
        SlideResponse slideResponse = SlideResponse.builder()
                .id(slide.getId())
                .title(slide.getTitle())
                .imageUrl(slide.getImageUrl())
                .publicId(slide.getPublicId())
                .link(slide.getLink())
                .description(slide.getDescription())
                .status(slide.getStatus())
                .order(slide.getOrder())
                .build();
        slideResponse.setCreatedAt(slide.getCreatedAt());
        slideResponse.setUpdatedAt(slide.getUpdatedAt());
        return slideResponse;
    }
}
