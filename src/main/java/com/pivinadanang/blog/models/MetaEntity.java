package com.pivinadanang.blog.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meta")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "viewport", length = 255)
    private String viewport = "width=device-width, initial-scale=1";

    @Column(name = "robots", length = 255)
    private String robots = "index, follow";

    @Column(name = "slug", length = 255, unique = true)
    private String slug;

    @Column(name = "og_title", length = 255)
    private String ogTitle;

    @Column(name = "og_description", columnDefinition = "TEXT")
    private String ogDescription;

    @Column(name = "og_image", length = 255)
    private String ogImage;

}