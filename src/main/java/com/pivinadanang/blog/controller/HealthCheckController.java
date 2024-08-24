package com.pivinadanang.blog.controller;


import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.services.category.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/healthcheck")
@AllArgsConstructor
public class HealthCheckController {
    private final CategoryService categoryService;
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            return ResponseEntity.ok("ok");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("failed");
        }
    }

}
