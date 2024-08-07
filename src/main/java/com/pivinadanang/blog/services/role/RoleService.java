package com.pivinadanang.blog.services.role;

import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    @Override
    public List<RoleEntity> getAllRoles() {
       return roleRepository.findAll();
    }
}
