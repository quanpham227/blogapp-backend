package com.pivinadanang.blog.services.category;

import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.repositories.CategoryRepository;

import com.pivinadanang.blog.repositories.PostRepository;

import java.util.List;
import java.util.stream.Collectors;


import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.post.IPostService;
import com.pivinadanang.blog.services.post.PostUtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final PostUtilityService postUtilityService;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getDescription() == null || categoryDTO.getDescription().isEmpty()) {
            categoryDTO.setDescription(categoryDTO.getName());
        }
        String categoryCode = postUtilityService.generateSlug(categoryDTO.getName());

        CategoryEntity newCategory = CategoryEntity
                .builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .code(categoryCode)
                .build();
        CategoryEntity category =  categoryRepository.save(newCategory);
        return CategoryResponse.fromCategory(category);
    }

    @Override
    public CategoryEntity getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> categories = categoryRepository.findCategoriesWithPostCount(PostStatus.PUBLISHED, PostVisibility.PUBLIC);
        return categories.stream()
                .map(dto -> CategoryResponse.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .code(dto.getCode())
                        .description(dto.getDescription())

                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(long categoryId, CategoryDTO categoryDTO) {
        CategoryEntity existingCategory = getCategoryById(categoryId);
        if(categoryDTO.getName() != null || !categoryDTO.getName().isEmpty()){
            if(!existingCategory.getName().equals(categoryDTO.getName())){
                if(categoryRepository.exitstsByName(categoryDTO.getName())){
                    throw new IllegalStateException("Category with name " + categoryDTO.getName() + " already exists");
                }
            }
            existingCategory.setName(categoryDTO.getName());
            String categoryCode = postUtilityService.generateSlug(categoryDTO.getName());
            existingCategory.setCode(categoryCode);
        }
        if(categoryDTO.getDescription() != null || !categoryDTO.getDescription().isEmpty()){
            existingCategory.setDescription(categoryDTO.getDescription());
        }

        CategoryEntity category = categoryRepository.save(existingCategory);
        return CategoryResponse.fromCategory(category);
    }

    @Override
    @Transactional
    public CategoryEntity deleteCategory(long id) throws Exception {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        List<PostEntity> posts = postRepository.findByCategory(category);
        if (!posts.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        } else {
            categoryRepository.deleteById(id);
            return  category;
        }
    }

    @Override
    public boolean existsCategoryByName(String name) {
        return categoryRepository.exitstsByName(name);
    }

    @Override
    public List<CategoryResponse> getTopCategoriesByPostCount(int limit) {
        Pageable pageable = PageRequest.of(0, limit); // Giới hạn số lượng là 3
        List<CategoryEntity> categories = categoryRepository.findTopCategoriesByPostCount(pageable);
        return categories.stream()
                .map(CategoryResponse::fromCategory)
                .toList();
    }
}
