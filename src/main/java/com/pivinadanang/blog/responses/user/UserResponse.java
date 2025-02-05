package com.pivinadanang.blog.responses.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.role.RoleResponse;
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

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("is_active")
    private boolean active;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    @JsonProperty("role")
    RoleResponse role;
    public static UserResponse fromUser(UserEntity user) {
        RoleResponse roleResponse = (user.getRole() != null) ? RoleResponse.fromRole(user.getRole()) : null;

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImage(user.getProfileImage())
                .active(user.isActive())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(roleResponse)
                .build();
    }
}
