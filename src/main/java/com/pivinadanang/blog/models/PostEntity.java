package com.pivinadanang.blog.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.text.Normalizer;
import java.util.regex.Pattern;

import com.pivinadanang.blog.enums.PostStatus;
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
public class PostEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "slug", length = 255)
    private String slug;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "thumbnail", length = 2048)
    private String thumbnail;

    @Column(name = "status")
    private PostStatus status;


    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private CategoryEntity category;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "post",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<FavouriteEntity> favorites = new ArrayList<>();


    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        if (this.title != null) {
            this.slug = generateSlug(this.title);
        }
        if (this.content != null) {
            this.excerpt = generateExcerpt(this.content);
        }
    }

    private String generateSlug(String title) {
        // Bước 1: Chuẩn hóa chuỗi, loại bỏ dấu và chuyển thành chữ thường
        String normalizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD);
        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalizedTitle).replaceAll("");
        // Bước 2: Chuyển đổi thành chữ thường, loại bỏ ký tự đặc biệt, và thay thế khoảng trắng bằng dấu gạch ngang
        slug = slug.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        return slug;
    }

    private String generateExcerpt(String content) {
        // Loại bỏ URL hình ảnh hoặc các URL khác
        String plainTextContent = content.replaceAll("http[s]?://\\S+\\.(png|jpg|jpeg|gif|svg)", "");
        // Cắt đoạn trích theo độ dài
        return plainTextContent.length() > 200 ? plainTextContent.substring(0, 200) + "..." : plainTextContent;
    }
}
