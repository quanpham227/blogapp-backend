package com.pivinadanang.blog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AboutDTO {
    @NotEmpty(message = "Category cannot be empty")
    private String title;
    private String content;
    @JsonProperty("image_url")
    private String imageUrl;
    private String address;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String email;
    @JsonProperty("working_hours")
    private String workingHours;
    @JsonProperty("facebook_link")
    private String facebookLink;
    private String youtube;
    @JsonProperty("vision_statement")
    private String visionStatement;
    @JsonProperty("founding_date")
    private String foundingDate;
    @JsonProperty("ceo_name")
    private String ceoName;
}
