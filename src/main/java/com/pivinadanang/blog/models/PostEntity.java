package com.pivinadanang.blog.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;


import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import jakarta.persistence.*;

import lombok.*;

import java.util.*;
@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
//Event-driven approach with Spring Data JPA
@EntityListeners(PostListener.class)
public class PostEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "slug", unique = true,nullable = false)
    private String slug;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "thumbnail", length = 2048)
    private String thumbnail;

    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @Column(name = "status")
    private PostStatus status;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private PostVisibility visibility;

    @Column(name = "revision_count")
    private int revisionCount = 0;

    @Column(name = "view_count")
    private int viewCount = 0;

    @Column(name = "ratings_count")
    private int ratingsCount = 0;

    @Column(name = "comment_count")
    private int commentCount = 0;

    @Column(name = "priority")
    private int priority = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "post",
            orphanRemoval = true,
            cascade = CascadeType.ALL, //(vis duj xoas 1 post thi xoa het comment)
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<FavouriteEntity> favorites = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "meta_id", referencedColumnName = "id")
    private MetaEntity meta;

    @OneToMany(mappedBy = "post",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<RatingEntity> ratings;


    @ManyToMany
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags = new HashSet<>();




    // Custom setter to ensure viewCount is not negative
    public void setViewCount(int viewCount) {
        if (viewCount < 0) {
            throw new IllegalArgumentException("View count cannot be negative");
        }
        this.viewCount = viewCount;
    }

    // Method to increment view count
    public void incrementViewCount() {
        this.viewCount++;
    }

    // Custom setter to ensure revisionCount is not negative
    public void setRevisionCount(int revisionCount) {
        if (revisionCount < 0) {
            throw new IllegalArgumentException("Revision count cannot be negative");
        }
        this.revisionCount = revisionCount;
    }

    // Method to increment revision count
    public void incrementRevisionCount() {
        this.revisionCount++;
    }
    public Long getCategoryId() {
        if (category != null) {
            return category.getId();
        } else {
            throw new IllegalStateException("Category is null");
        }
    }
}
