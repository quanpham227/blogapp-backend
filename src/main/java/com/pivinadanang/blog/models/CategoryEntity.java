package com.pivinadanang.blog.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


@Entity
@Table(name = "categories")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false, length = 100)
    private String name;

    @Column (name="code")
    private String code;


    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private Set<PostEntity> posts = new HashSet<>();



    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        if (this.code == null || this.code.isEmpty()) {
            this.code = generateCode(this.name);
        }

    }
    private String generateCode(String categoryName) {
        // Bước 1: Chuẩn hóa chuỗi, loại bỏ dấu và chuyển thành chữ thường
        String normalizedTitle = Normalizer.normalize(categoryName, Normalizer.Form.NFD);
        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalizedTitle).replaceAll("");
        // Bước 2: Chuyển đổi thành chữ thường, loại bỏ ký tự đặc biệt, và thay thế khoảng trắng bằng dấu gạch ngang
        slug = slug.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        return slug;
    }

}
