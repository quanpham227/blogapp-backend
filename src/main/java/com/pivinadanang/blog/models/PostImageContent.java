package com.pivinadanang.blog.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_image_content")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostImageContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url")
    @JsonProperty("image_url")
    private String imageUrl;

    @Column(name = "file_id")
    private String fileId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private PostEntity post;
}
