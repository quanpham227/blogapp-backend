package com.pivinadanang.blog.responses.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("is_active")
    private boolean active;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    @JsonProperty("role")
    RoleEntity role;
    public static UserResponse fromUser(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .active(user.isActive())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(user.getRole())
                .build();
    }
}
