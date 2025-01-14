package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.role.RoleResponse;
import com.pivinadanang.blog.services.role.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;
    private final LocalizationUtils localizationUtils;

    @RequestMapping("")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        if (roles == null) {
            roles = Collections.emptyList();
        }
        String message = roles.isEmpty() ? "" : "Get all roles successfully";
        return ResponseEntity.ok(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(roles)
                .build());
    }
}

