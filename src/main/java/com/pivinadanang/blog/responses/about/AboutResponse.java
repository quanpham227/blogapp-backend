package com.pivinadanang.blog.responses.about;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.AboutEntity;
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

    public static AboutResponse fromAbout(AboutEntity about) {
        return AboutResponse.builder()
                .id(about.getId())
                .title(about.getTitle())
                .content(about.getContent())
                .imageUrl(about.getImageUrl() != null ? about.getImageUrl() : "") // Xử lý null với giá trị mặc định rỗng
                .address(about.getAddress() != null ? about.getAddress() : "") // Xử lý null với giá trị mặc định rỗng
                .phoneNumber(about.getPhoneNumber() != null ? about.getPhoneNumber() : "") // Xử lý null với giá trị mặc định rỗng
                .email(about.getEmail() != null ? about.getEmail() : "") // Xử lý null với giá trị mặc định rỗng
                .workingHours(about.getWorkingHours() != null ? about.getWorkingHours() : "") // Xử lý null với giá trị mặc định rỗng
                .facebookLink(about.getFacebookLink() != null ? about.getFacebookLink() : "") // Xử lý null với giá trị mặc định rỗng
                .youtube(about.getYoutube() != null ? about.getYoutube() : "") // Xử lý null với giá trị mặc định rỗng
                .visionStatement(about.getVisionStatement() != null ? about.getVisionStatement() : "") // Xử lý null với giá trị mặc định rỗng
                .foundingDate(about.getFoundingDate() != null ? about.getFoundingDate() : "") // Xử lý null với giá trị mặc định rỗng
                .ceoName(about.getCeoName() != null ? about.getCeoName() : "") // Xử lý null với giá trị mặc định rỗng
                .build();
    }

}
