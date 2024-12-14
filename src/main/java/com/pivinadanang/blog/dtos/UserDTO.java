package com.pivinadanang.blog.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO extends SocialAccountDTO{
    @JsonProperty("full_name")
    @Size(max = 100, message = "Full name must be less than or equal to 100 characters")
    private String fullName;

    @JsonProperty("phone_number")
    @Size(max = 15, message = "Phone number must be less than or equal to 15 characters")
    private String phoneNumber = "";

    @JsonProperty("email")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be  than or equal to 100 characters")
    private String email = "";

    @NotBlank(message = "Password cannot be blank")
    @Size(max = 200, message = "Password must be less than or equal to 200 characters")
    private String password = "";

    @JsonProperty("retype_password")
    @Size(max = 200, message = "Retype password must be less than or equal to 200 characters")
    private String retypePassword = "";

    @JsonProperty("facebook_account_id")
    @Size(max = 255, message = "Facebook account ID must be less than or equal to 255 characters")
    private String facebookAccountId;

    @JsonProperty("google_account_id")
    @Size(max = 255, message = "Google account ID must be less than or equal to 255 characters")
    private String googleAccountId;

    @JsonProperty("role_id")
    private Long roleId;
}
