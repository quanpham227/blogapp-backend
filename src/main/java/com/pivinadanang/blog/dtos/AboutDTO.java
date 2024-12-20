package com.pivinadanang.blog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AboutDTO {
    @NotEmpty(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 10000, message = "description must be less than or equal to 10000 characters")
    private String content;

    @JsonProperty("image_url")
    @Size(max = 2048, message = "Image URL cannot exceed 2048 characters")
    private String imageUrl;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @JsonProperty("phone_number")
    @Size(max = 100, message = "Phone number cannot exceed 100 characters")
    private String phoneNumber;

    @NotEmpty(message = "Email cannot be empty")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @JsonProperty("working_hours")
    @Size(max = 255, message = "Working hours cannot exceed 255 characters")
    private String workingHours;

    @JsonProperty("facebook_link")
    @Size(max = 255, message = "Facebook link cannot exceed 255 characters")
    private String facebookLink;

    @Size(max = 255, message = "YouTube link cannot exceed 255 characters")
    private String youtube;

    @Size(max = 10000, message = "Vision statement cannot exceed 10000 characters")
    @JsonProperty("vision_statement")
    private String visionStatement;

    @JsonProperty("founding_date")
    @Size(max = 255, message = "Founding date cannot exceed 255 characters")
    private String foundingDate;

    @JsonProperty("ceo_name")
    @Size(max = 100, message = "CEO name cannot exceed 100 characters")
    private String ceoName;

}
