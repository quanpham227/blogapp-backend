package com.pivinadanang.blog.services.category;

import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.repositories.CategoryRepository;

import com.pivinadanang.blog.repositories.PostRepository;

import java.util.List;


import com.pivinadanang.blog.responses.category.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public CategoryEntity createCategory(CategoryDTO categoryDTO) {
        categoryDTO.generateSlug();
        CategoryEntity newCategory = CategoryEntity
                .builder()
                .name(categoryDTO.getName())
                .code(categoryDTO.getCode())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public CategoryEntity getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(CategoryResponse::fromCategory).toList();
    }

    @Override
    @Transactional
    public CategoryEntity updateCategory(long categoryId, CategoryDTO categoryDTO) {
        CategoryEntity existingCategory = getCategoryById(categoryId);

        // Cập nhật name và code từ DTO
        existingCategory.setName(categoryDTO.getName());
        categoryDTO.generateSlug();
        existingCategory.setCode(categoryDTO.getCode());

        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public CategoryEntity deleteCategory(long id) throws Exception {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        List<PostEntity> posts = postRepository.findByCategory(category);
        if (!posts.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        } else {
            categoryRepository.deleteById(id);
            return  category;
        }
    }
}
