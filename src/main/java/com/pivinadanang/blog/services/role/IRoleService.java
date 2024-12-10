package com.pivinadanang.blog.services.role;

import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.responses.role.RoleResponse;

import java.util.List;

public interface IRoleService {
    List<RoleResponse> getAllRoles();
    RoleEntity getRoleById(Long id) throws Exception;
}
