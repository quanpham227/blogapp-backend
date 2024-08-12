package com.pivinadanang.blog.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    @NotEmpty(message = "name cannot be empty")
    private String name;

    private String description;

    private String logo;


    private String fileId;
}
