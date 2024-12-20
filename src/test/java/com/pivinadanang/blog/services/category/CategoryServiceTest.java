package com.pivinadanang.blog.services.category;


import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.repositories.CategoryRepository;
import com.pivinadanang.blog.repositories.PostRepository;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.services.post.PostUtilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostUtilityService postUtilityService;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCategory_Success() {
        // Mock CategoryDTO
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("Category 1")
                .description("Description 1")
                .build();

        // Mock CategoryEntity
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1L)
                .name("Category 1")
                .description("Description 1")
                .code("category-1")
                .build();

        when(postUtilityService.generateSlug("Category 1")).thenReturn("category-1");
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);

        // Call the service method
        CategoryResponse categoryResponse = categoryService.createCategory(categoryDTO);

        // Assertions
        assertEquals(1L, categoryResponse.getId());
        assertEquals("Category 1", categoryResponse.getName());
        assertEquals("Description 1", categoryResponse.getDescription());
        assertEquals("category-1", categoryResponse.getCode());
    }

    @Test
    public void testGetCategoryById_Success() {
        // Mock CategoryEntity
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1L)
                .name("Category 1")
                .description("Description 1")
                .code("category-1")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));

        // Call the service method
        CategoryEntity category = categoryService.getCategoryById(1L);

        // Assertions
        assertEquals(1L, category.getId());
        assertEquals("Category 1", category.getName());
        assertEquals("Description 1", category.getDescription());
        assertEquals("category-1", category.getCode());
    }

    @Test
    public void testGetCategoryById_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(1L);
        });

        // Assertions
        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void testGetAllCategories() {
        // Mock CategoryResponse
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Category 1")
                .description("Description 1")
                .code("category-1")
                .build();

        when(categoryRepository.findCategoriesWithPostCount(PostStatus.PUBLISHED, PostVisibility.PUBLIC))
                .thenReturn(Collections.singletonList(categoryResponse));

        // Call the service method
        List<CategoryResponse> categories = categoryService.getAllCategories();

        // Assertions
        assertEquals(1, categories.size());
        CategoryResponse response = categories.get(0);
        assertEquals(1L, response.getId());
        assertEquals("Category 1", response.getName());
        assertEquals("Description 1", response.getDescription());
        assertEquals("category-1", response.getCode());
    }

    @Test
    public void testUpdateCategory_Success() {
        // Mock CategoryDTO
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("Updated Category")
                .description("Updated Description")
                .build();

        // Mock CategoryEntity
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1L)
                .name("Category 1")
                .description("Description 1")
                .code("category-1")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
        when(postUtilityService.generateSlug("Updated Category")).thenReturn("updated-category");
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);

        // Call the service method
        CategoryResponse categoryResponse = categoryService.updateCategory(1L, categoryDTO);

        // Assertions
        assertEquals(1L, categoryResponse.getId());
        assertEquals("Updated Category", categoryResponse.getName());
        assertEquals("Updated Description", categoryResponse.getDescription());
        assertEquals("updated-category", categoryResponse.getCode());
    }

    @Test
    public void testUpdateCategory_NotFound() {
        // Mock CategoryDTO
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("Updated Category")
                .description("Updated Description")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, categoryDTO);
        });

        // Assertions
        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void testDeleteCategory_Success() throws Exception {
        // Mock CategoryEntity
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1L)
                .name("Category 1")
                .description("Description 1")
                .code("category-1")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
        when(postRepository.findByCategory(categoryEntity)).thenReturn(Collections.emptyList());

        // Call the service method
        CategoryEntity deletedCategory = categoryService.deleteCategory(1L);

        // Assertions
        assertEquals(1L, deletedCategory.getId());
        assertEquals("Category 1", deletedCategory.getName());
        assertEquals("Description 1", deletedCategory.getDescription());
        assertEquals("category-1", deletedCategory.getCode());
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteCategory_NotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        // Assertions
        assertEquals("Category not found", exception.getMessage());
    }

    @Test
    public void testDeleteCategory_WithAssociatedPosts() {
        // Mock CategoryEntity
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1L)
                .name("Category 1")
                .description("Description 1")
                .code("category-1")
                .build();

        // Mock PostEntity
        PostEntity postEntity = PostEntity.builder()
                .id(1L)
                .title("Post 1")
                .category(categoryEntity)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
        when(postRepository.findByCategory(categoryEntity)).thenReturn(Collections.singletonList(postEntity));

        // Call the service method and expect an exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        // Assertions
        assertEquals("Cannot delete category with associated products", exception.getMessage());
    }

    @Test
    public void testExistsCategoryByName() {
        when(categoryRepository.exitstsByName("Category 1")).thenReturn(true);

        // Call the service method
        boolean exists = categoryService.existsCategoryByName("Category 1");

        // Assertions
        assertTrue(exists);
    }

    @Test
    public void testGetTopCategoriesByPostCount() {
        // Mock CategoryEntity
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1L)
                .name("Category 1")
                .description("Description 1")
                .code("category-1")
                .build();

        Pageable pageable = PageRequest.of(0, 3);
        when(categoryRepository.findTopCategoriesByPostCount(pageable)).thenReturn(Collections.singletonList(categoryEntity));

        // Call the service method
        List<CategoryResponse> categories = categoryService.getTopCategoriesByPostCount(3);

        // Assertions
        assertEquals(1, categories.size());
        CategoryResponse categoryResponse = categories.get(0);
        assertEquals(1L, categoryResponse.getId());
        assertEquals("Category 1", categoryResponse.getName());
        assertEquals("Description 1", categoryResponse.getDescription());
        assertEquals("category-1", categoryResponse.getCode());
    }
}