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

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "meta_keywords", length = 255)
    private String metaKeywords;

    @Column(name = "og_title", length = 255)
    private String ogTitle;

    @Column(name = "og_description", columnDefinition = "TEXT")
    private String ogDescription;

    @Column(name = "og_image", length = 255)
    private String ogImage;
}