package com.pivinadanang.blog.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SocialAccountDTO {

    @JsonProperty("facebook_account_id")
    protected String facebookAccountId;

    @JsonProperty("google_account_id")
    protected String googleAccountId;

    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }

    // Phương thức kiểm tra xem người dùng có phải là người dùng đăng nhập xã hội hay không
    public boolean isSocialLogin() {
        return (facebookAccountId != null && !facebookAccountId.isEmpty()) ||
                (googleAccountId != null && !googleAccountId.isEmpty());
    }
}
