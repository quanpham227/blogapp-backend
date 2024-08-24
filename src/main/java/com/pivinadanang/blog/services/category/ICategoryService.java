package com.pivinadanang.blog.services.category;

import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.category.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryDTO category);
    CategoryEntity getCategoryById(long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(long categoryId, CategoryDTO category);
    CategoryEntity deleteCategory(long id) throws Exception;

    boolean existsCategoryByName(String name);
}
