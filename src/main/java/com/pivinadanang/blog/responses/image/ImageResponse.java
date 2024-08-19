package com.pivinadanang.blog.responses.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.BaseResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import jakarta.persistence.Column;
import lombok.*;

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


    public static ImageResponse fromImage (ImageEntity image) {
        ImageResponse imageResponse = ImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .publicId(image.getPublicId())
                .fileName(image.getFileName())
                .objectType(image.getObjectType())
                .fileType(image.getFileType())
                .fileSize(image.getFileSize())
                .build();
        imageResponse.setCreatedAt(image.getCreatedAt());
        imageResponse.setUpdatedAt(image.getUpdatedAt());
        return imageResponse;
    }
}
