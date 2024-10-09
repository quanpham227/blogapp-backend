package com.pivinadanang.blog.responses.role;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    private String name;

    public static RoleResponse fromRole(String role) {
        return RoleResponse.builder()
                .name(role)
                .build();
    }
}
