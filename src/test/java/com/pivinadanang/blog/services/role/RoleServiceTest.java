package com.pivinadanang.blog.services.role;


import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.repositories.RoleRepository;
import com.pivinadanang.blog.responses.role.RoleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoles() {
        RoleEntity role1 = new RoleEntity();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        RoleEntity role2 = new RoleEntity();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<RoleResponse> roles = roleService.getAllRoles();

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals("ROLE_USER", roles.get(0).getName());
        assertEquals("ROLE_ADMIN", roles.get(1).getName());
    }

    @Test
    void testGetRoleById_Success() throws Exception {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("ROLE_USER");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleEntity foundRole = roleService.getRoleById(1L);

        assertNotNull(foundRole);
        assertEquals(1L, foundRole.getId());
        assertEquals("ROLE_USER", foundRole.getName());
    }

    @Test
    void testGetRoleById_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            roleService.getRoleById(1L);
        });

        assertEquals("User not found", exception.getMessage());
    }
}