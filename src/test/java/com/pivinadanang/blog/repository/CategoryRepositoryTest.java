package com.pivinadanang.blog.repository;

import com.pivinadanang.blog.BlogApplication;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.repositories.CategoryRepository;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = BlogApplication.class)
@Transactional
@Rollback
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        // Add sample data before each test case
        CategoryEntity category1 = CategoryEntity.builder()
                .name("Category 1")
                .description("First Category")
                .code("CAT1")
                .build();

        CategoryEntity category2 = CategoryEntity.builder()
                .name("Category 2")
                .description("Second Category")
                .code("CAT2")
                .build();

        categoryRepository.saveAll(List.of(category1, category2));
    }

    @Test
    void testExistsByName_whenNameExists_thenReturnTrue() {
        // Act
        boolean exists = categoryRepository.exitstsByName("Category 1");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByName_whenNameDoesNotExist_thenReturnFalse() {
        // Act
        boolean exists = categoryRepository.exitstsByName("Non-existent Category");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void testFindTopCategoriesByPostCount() {
        // Act
        List<CategoryEntity> topCategories = categoryRepository.findTopCategoriesByPostCount(PageRequest.of(0, 10));

        // Assert
        assertThat(topCategories).isNotEmpty();
    }

    @Test
    void testFindCategoriesWithPostCount() {
        // Act
        List<CategoryResponse> categoriesWithPostCount = categoryRepository.findCategoriesWithPostCount(PostStatus.PUBLISHED, PostVisibility.PUBLIC);

        // Assert
        assertThat(categoriesWithPostCount).isNotEmpty();
    }

    @Test
    void testSave_whenNewCategory_thenSaveSuccessfully() {
        // Arrange
        CategoryEntity newCategory = CategoryEntity.builder()
                .name("New Category")
                .description("Newly added category")
                .code("NEW_CAT")
                .build();

        // Act
        CategoryEntity savedCategory = categoryRepository.save(newCategory);

        // Assert
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("New Category");
    }

    @Test
    void testFindById_whenIdExists_thenReturnCategory() {
        // Arrange
        CategoryEntity existingCategory = categoryRepository.findAll().get(0);
        Long existingId = existingCategory.getId();

        // Act
        CategoryEntity foundCategory = categoryRepository.findById(existingId).orElse(null);

        // Assert
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getId()).isEqualTo(existingId);
    }

    @Test
    void testFindById_whenIdDoesNotExist_thenReturnNull() {
        // Act
        CategoryEntity foundCategory = categoryRepository.findById(999L).orElse(null);

        // Assert
        assertThat(foundCategory).isNull();
    }

    @Test
    void testDeleteById_whenIdExists_thenDeleteSuccessfully() {
        // Arrange
        CategoryEntity existingCategory = categoryRepository.findAll().get(0);
        Long existingId = existingCategory.getId();

        // Act
        categoryRepository.deleteById(existingId);

        // Assert
        assertThat(categoryRepository.findById(existingId)).isEmpty();
    }


}