package com.pivinadanang.blog.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.MetaEntity;
import lombok.*;

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
    @JsonProperty("meta_keywords")
    private String metaKeywords;
    @JsonProperty("og_title")
    private String ogTitle;
    @JsonProperty("og_description")
    private String ogDescription;
    @JsonProperty("og_image")
    private String ogImage;

    public static MetaResponse fromMeta(MetaEntity meta) {
        MetaResponse metaResponse =  MetaResponse.builder()
                .metaTitle(meta.getMetaTitle())
                .metaDescription(meta.getMetaDescription())
                .metaKeywords(meta.getMetaKeywords())
                .ogTitle(meta.getOgTitle())
                .ogDescription(meta.getOgDescription())
                .ogImage(meta.getOgImage())
                .build();

        metaResponse.setCreatedAt(meta.getCreatedAt());
        metaResponse.setUpdatedAt(meta.getUpdatedAt());
        return metaResponse;
    }
}
