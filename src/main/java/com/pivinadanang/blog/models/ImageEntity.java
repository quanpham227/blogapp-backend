package com.pivinadanang.blog.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ImageEntity extends BaseEntity{
    public static final int MAXIMUM_IMAGES = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 2048)
    private String imageUrl;

    @Column(name = "public_id", unique = true, length = 255)
    private String publicId;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "object_type", nullable = false, length = 255)
    private String objectType;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

}
