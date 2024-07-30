package com.pivinadanang.blog.services.category;

import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.responses.post.PostResponse;

import java.util.List;

public interface ICategoryService {
    CategoryEntity createCategory(CategoryDTO category);
    CategoryEntity getCategoryById(long id);
    List<CategoryResponse> getAllCategories();
    CategoryEntity updateCategory(long categoryId, CategoryDTO category);
    CategoryEntity deleteCategory(long id) throws Exception;

}
