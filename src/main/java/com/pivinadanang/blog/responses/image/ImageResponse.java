package com.pivinadanang.blog.responses.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponse extends BaseResponse {

    private Long id;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("object_type")
    private String objectType;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("is_used")
    private Boolean isUsed;

    @JsonProperty("usage_count")
    private Integer usageCount;

    public static ImageResponse fromImage (ImageEntity image) {
        ImageResponse imageResponse = ImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .publicId(image.getPublicId())
                .fileName(image.getFileName())
                .objectType(image.getObjectType())
                .fileType(image.getFileType())
                .fileSize(image.getFileSize())
                .isUsed(image.getIsUsed())
                .usageCount(image.getUsageCount())
                .build();
        imageResponse.setCreatedAt(image.getCreatedAt() != null ? image.getCreatedAt() :  LocalDateTime.now());
        imageResponse.setUpdatedAt(image.getUpdatedAt() != null ? image.getUpdatedAt() :  LocalDateTime.now());
        return imageResponse;
    }
}
