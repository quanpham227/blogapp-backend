package com.pivinadanang.blog.responses.about;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.AboutEntity;
import com.pivinadanang.blog.models.SlideEntity;
import com.pivinadanang.blog.responses.slide.SlideResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AboutResponse {
    private Long id; // ID của trang About

    private String title; // Tiêu đề của trang About

    private String content; // Nội dung của trang About

    @JsonProperty("image_url")
    private String imageUrl; // URL của hình ảnh đại diện cho trang About

    private String address; // Địa chỉ của công ty

    @JsonProperty("phone_number")
    private String phoneNumber; // Số điện thoại của công ty

    private String email; // Email liên hệ của công ty

    @JsonProperty("working_hours")
    private String workingHours; // Giờ làm việc của công ty

    @JsonProperty("facebook_link")
    private String facebookLink; // Liên kết đến trang Facebook của công ty

    private String youtube; // Liên kết đến trang YouTube của công ty

    @JsonProperty("vision_statement")
    private String visionStatement; // Tuyên bố tầm nhìn của công ty

    @JsonProperty("founding_date")
    private String foundingDate; // Ngày thành lập công ty

    @JsonProperty("ceo_name")
    private String ceoName; // Tên của CEO hoặc người đứng đầu công ty

     public static AboutResponse fromAbout (AboutEntity about){
         return AboutResponse.builder()
                 .id(about.getId())
                 .title(about.getTitle())
                 .content(about.getContent())
                 .imageUrl(about.getImageUrl())
                 .address(about.getAddress())
                 .phoneNumber(about.getPhoneNumber())
                 .email(about.getEmail())
                 .workingHours(about.getWorkingHours())
                 .facebookLink(about.getFacebookLink())
                 .youtube(about.getYoutube())
                 .visionStatement(about.getVisionStatement())
                 .foundingDate(about.getFoundingDate())
                 .ceoName(about.getCeoName())
                 .build();
     }
}
