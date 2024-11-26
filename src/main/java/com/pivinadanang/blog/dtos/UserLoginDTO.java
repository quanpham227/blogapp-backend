package com.pivinadanang.blog.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginDTO {

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(max = 200, message = "Password must be less than or equal to 200 characters")
    private String password;

    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    private String googleAccountId;

    @JsonProperty("full_name")
    private String fullName;

    // Profile image URL, not mandatory, can be blank
    @JsonProperty("profile_image")
    private String profileImage;

    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
    // Kiểm tra facebookAccountId có hợp lệ không
    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    // Kiểm tra googleAccountId có hợp lệ không
    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
