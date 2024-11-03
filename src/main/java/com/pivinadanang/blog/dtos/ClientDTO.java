package com.pivinadanang.blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    @NotEmpty(message = "name cannot be empty")
    @Size(max = 50, message = "name must be less than or equal to 50 characters")
    private String name;

    @Size(max = 10000, message = "description must be less than or equal to 10000 characters")
    private String description;

    @Size(max = 2048, message = "logo URL must be less than or equal to 2048 characters")
    private String logo;

    @JsonProperty("public_id")
    @Size(max = 255, message = "public_id must be less than or equal to 255 characters")
    private String publicId;

}
