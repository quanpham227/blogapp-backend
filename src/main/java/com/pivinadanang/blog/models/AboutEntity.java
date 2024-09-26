package com.pivinadanang.blog.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "about")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AboutEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID của trang About

    @Column(name = "unique_key", nullable = false, unique = true)
    private String uniqueKey; // Khóa duy nhất để xác định bản ghi About

    @Column(name = "title", nullable = false, length = 255)
    private String title; // Tiêu đề của trang About

    @Column(name = "content",  columnDefinition = "TEXT")
    private String content; // Nội dung của trang About

    @Column(name = "image_url", length = 2048)
    private String imageUrl; // URL của hình ảnh đại diện cho trang About

    @Column(name = "address", length = 255)
    private String address; // Địa chỉ của công ty

    @Column(name = "phone_number", length = 20)
    private String phoneNumber; // Số điện thoại của công ty

    @Column(name = "email", length = 100)
    private String email; // Email liên hệ của công ty

    @Column(name = "working_hours", length = 255)
    private String workingHours; // Giờ làm việc của công ty

    @Column(name = "facebook_link", length = 255)
    private String facebookLink; // Liên kết đến trang Facebook của công ty

    @Column(name = "youtube", length = 255)
    private String youtube; // Liên kết đến trang YouTube của công ty

    @Column(name = "vision_statement", columnDefinition = "TEXT")
    private String visionStatement; // Tuyên bố tầm nhìn của công ty

    @Column(name = "founding_date", length = 100)
    private String foundingDate; // Ngày thành lập công ty

    @Column(name = "ceo_name", length = 50)
    private String ceoName; // Tên của CEO hoặc người đứng đầu công ty
}