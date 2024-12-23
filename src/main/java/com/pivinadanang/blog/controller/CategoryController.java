package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.services.category.CategoryService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@Validated
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;


    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> insertCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws Exception {

        if(categoryService.existsCategoryByName(categoryDTO.getName())){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());

        }
        CategoryResponse category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(category)
                .build());
    }

    //Hiện tất cả các categories
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllCategories(
    ) {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of categories successfully")
                .status(HttpStatus.OK)
                .data(categories)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCategoryById(
            @PathVariable("id") Long categoryId
    ) {
        CategoryEntity existingCategory = categoryService.getCategoryById(categoryId);
        if (existingCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .message("Category not found")
                            .status(HttpStatus.NOT_FOUND)
                            .build());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingCategory)
                .message("Get category information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateCategory (@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) throws Exception {
       CategoryResponse categoryResponse =  categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .status(HttpStatus.OK)
                .data(categoryResponse)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY))
                .build());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable Long id) throws Exception{
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY, id))
                        .build());
    }

    @GetMapping("/top")
    public ResponseEntity<ResponseObject> getTopCategoriesByPostCount() {
        List<CategoryResponse> topCategories = categoryService.getTopCategoriesByPostCount(3);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get top 3 categories successfully")
                .status(HttpStatus.OK)
                .data(topCategories)
                .build());
    }
}
