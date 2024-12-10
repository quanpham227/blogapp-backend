package com.pivinadanang.blog.services.role;

import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.repositories.RoleRepository;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.responses.role.RoleResponse;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<RoleResponse> getAllRoles() {
        List<RoleEntity> roles = roleRepository.findAll();
        return RoleResponse.fromRoles(roles);
    }

    @Override
    public RoleEntity getRoleById(Long id) throws Exception {
        return roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }
}
