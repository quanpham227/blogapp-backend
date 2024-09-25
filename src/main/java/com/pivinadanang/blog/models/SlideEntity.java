package com.pivinadanang.blog.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "slides")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlideEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "image_url", nullable = false, length = 2048)
    private String imageUrl;

    @Column (name="public_id", nullable = false)
    private String publicId;

    @Column (name="description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "link", length = 2048)
    private String link;

    @Column(name = "status" , nullable = false)
    private Boolean status = true;

    @Column(name = "display_order")
    private Integer order = 0;
}
