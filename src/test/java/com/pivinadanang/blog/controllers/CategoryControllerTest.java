package com.pivinadanang.blog.controllers;


import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.controller.CategoryController;
import com.pivinadanang.blog.dtos.CategoryDTO;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import com.pivinadanang.blog.services.category.CategoryService;
import com.pivinadanang.blog.ultils.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocalizationUtils localizationUtils;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsertCategory() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("New Category");

        when(categoryService.existsCategoryByName(categoryDTO.getName())).thenReturn(false);
        CategoryResponse categoryResponse = new CategoryResponse();
        when(categoryService.createCategory(categoryDTO)).thenReturn(categoryResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY)).thenReturn("Insert category successfully");

        ResponseEntity<ResponseObject> responseEntity = categoryController.insertCategory(categoryDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Insert category successfully", responseEntity.getBody().getMessage());
        assertEquals(categoryResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testGetAllCategories() {
        List<CategoryResponse> categories = Arrays.asList(new CategoryResponse(), new CategoryResponse());

        when(categoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<ResponseObject> responseEntity = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get list of categories successfully", responseEntity.getBody().getMessage());
        assertEquals(categories, responseEntity.getBody().getData());
    }

    @Test
    public void testGetCategoryById() {
        Long categoryId = 1L;
        CategoryEntity categoryEntity = new CategoryEntity();

        when(categoryService.getCategoryById(categoryId)).thenReturn(categoryEntity);

        ResponseEntity<ResponseObject> responseEntity = categoryController.getCategoryById(categoryId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get category information successfully", responseEntity.getBody().getMessage());
        assertEquals(categoryEntity, responseEntity.getBody().getData());
    }

    @Test
    public void testUpdateCategory() throws Exception {
        Long id = 1L;
        CategoryDTO categoryDTO = new CategoryDTO();
        CategoryResponse categoryResponse = new CategoryResponse();

        when(categoryService.updateCategory(id, categoryDTO)).thenReturn(categoryResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY)).thenReturn("Update category successfully");

        ResponseEntity<ResponseObject> responseEntity = categoryController.updateCategory(id, categoryDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Update category successfully", responseEntity.getBody().getMessage());
        assertEquals(categoryResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testDeleteCategory() throws Exception {
        Long id = 1L;

        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY, id)).thenReturn("Delete category successfully");

        ResponseEntity<ResponseObject> responseEntity = categoryController.deleteCategory(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete category successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetTopCategoriesByPostCount() {
        List<CategoryResponse> topCategories = Arrays.asList(new CategoryResponse(), new CategoryResponse());

        when(categoryService.getTopCategoriesByPostCount(3)).thenReturn(topCategories);

        ResponseEntity<ResponseObject> responseEntity = categoryController.getTopCategoriesByPostCount();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get top 3 categories successfully", responseEntity.getBody().getMessage());
        assertEquals(topCategories, responseEntity.getBody().getData());
    }
}