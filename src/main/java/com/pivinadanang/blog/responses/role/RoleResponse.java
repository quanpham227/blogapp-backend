package com.pivinadanang.blog.responses.role;
import com.pivinadanang.blog.models.RoleEntity;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    Long id;
    private String name;

    public static RoleResponse fromRole(RoleEntity role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
    public static List<RoleResponse> fromRoles(List<RoleEntity> roles) {
        return roles == null ?
                List.of() : // Trả về danh sách rỗng nếu roles là null
                roles.stream()
                        .map(RoleResponse::fromRole)
                        .collect(Collectors.toList());
    }
}
