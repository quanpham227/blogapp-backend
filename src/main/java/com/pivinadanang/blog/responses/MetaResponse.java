package com.pivinadanang.blog.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.MetaEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaResponse extends BaseResponse {
    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("og_title")
    private String ogTitle;
    @JsonProperty("og_description")
    private String ogDescription;
    @JsonProperty("og_image")
    private String ogImage;
    private String viewport;
    private String robots;
    private String slug;



    public static MetaResponse fromMeta(MetaEntity meta) {
        MetaResponse metaResponse =  MetaResponse.builder()
                .metaTitle(meta.getMetaTitle())
                .metaDescription(meta.getMetaDescription())
                .ogTitle(meta.getOgTitle())
                .ogDescription(meta.getOgDescription())
                .ogImage(meta.getOgImage())
                .viewport(meta.getViewport())
                .robots(meta.getRobots())
                .slug(meta.getSlug())
                .build();

        metaResponse.setCreatedAt(meta.getCreatedAt() != null ? meta.getCreatedAt() : LocalDateTime.now());
        metaResponse.setUpdatedAt(meta.getUpdatedAt() != null ? meta.getUpdatedAt() : LocalDateTime.now());
        return metaResponse;
    }
}
