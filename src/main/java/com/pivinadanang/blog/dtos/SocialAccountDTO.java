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

    @JsonProperty("provider")
    @NotEmpty(message = "Provider cannot be empty")
    @Size(max = 20, message = "Provider must be less than or equal to 20 characters")
    protected String provider;

    @JsonProperty("provider_id")
    @NotEmpty(message = "Provider ID cannot be empty")
    @Size(max = 50, message = "Provider ID must be less than or equal to 50 characters")
    protected String providerId;

    @JsonProperty("email")
    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must be less than or equal to 150 characters")
    protected String email;

    @JsonProperty("name")
    @Size(max = 150, message = "Name must be less than or equal to 150 characters")
    protected String name;

    public boolean isFacebookAccountIdValid() {
        return provider.equalsIgnoreCase("facebook") && providerId != null && !providerId.isEmpty();
    }

    public boolean isGoogleAccountIdValid() {
        return provider.equalsIgnoreCase("google") && providerId != null && !providerId.isEmpty();
    }

    // Phương thức kiểm tra xem người dùng có phải là người dùng đăng nhập xã hội hay không
    public boolean isSocialLogin() {
        return (provider.equalsIgnoreCase("facebook") && providerId != null && !providerId.isEmpty()) ||
                (provider.equalsIgnoreCase("google") && providerId != null && !providerId.isEmpty());
    }
}
