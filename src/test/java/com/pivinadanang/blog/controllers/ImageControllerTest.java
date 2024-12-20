package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.controller.ImageController;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.image.ImageListResponse;
import com.pivinadanang.blog.responses.image.ImageResponse;
import com.pivinadanang.blog.services.image.IImageService;
import com.pivinadanang.blog.ultils.MessageKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ImageControllerTest {

    @Mock
    private LocalizationUtils localizationUtils;

    @Mock
    private IImageService fileUploadService;

    @InjectMocks
    private ImageController imageController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetImages() {
        String keyword = "test";
        String objectType = "unused";
        int page = 0;
        int limit = 24;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse());
        Page<ImageResponse> imagePage = new PageImpl<>(imageResponses, pageable, imageResponses.size());
        Long totalFileSize = 100L;

        when(fileUploadService.getUnusedImages(pageable)).thenReturn(imagePage);
        when(fileUploadService.getTotalFileSize()).thenReturn(totalFileSize);

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages(keyword, objectType, page, limit);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get images successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponses, ((ImageListResponse) responseEntity.getBody().getData()).getImages());
        assertEquals(totalFileSize, ((ImageListResponse) responseEntity.getBody().getData()).getTotalFileSizes());
    }

    @Test
    public void testGetImage() throws Exception {
        Long imageId = 1L;
        ImageResponse imageResponse = new ImageResponse();

        when(fileUploadService.getImage(imageId)).thenReturn(imageResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.GET_IMAGE_SUCCESSFULLY)).thenReturn("Get image successfully");

        ResponseEntity<ResponseObject> responseEntity = imageController.getImage(imageId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get image successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testUploadImages() throws Exception {
        List<MultipartFile> files = Collections.singletonList(mock(MultipartFile.class));
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse());

        when(fileUploadService.uploadImages("objectType", files)).thenReturn(imageResponses);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY)).thenReturn("Upload image successfully");

        ResponseEntity<?> responseEntity = imageController.uploadImages("objectType", files);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Upload image successfully", ((ResponseObject) responseEntity.getBody()).getMessage());
        assertEquals(imageResponses, ((ResponseObject) responseEntity.getBody()).getData());
    }

    @Test
    public void testUploadImage() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        ImageResponse imageResponse = new ImageResponse();

        when(fileUploadService.uploadImage("objectType", file)).thenReturn(imageResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY)).thenReturn("Upload image successfully");

        ResponseEntity<ResponseObject> responseEntity = imageController.uploadImage("objectType", file);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Upload image successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testDeleteImages() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);

        doNothing().when(fileUploadService).deleteImages(ids);
        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_IMAGE_SUCCESSFULLY, ids)).thenReturn("Delete image successfully");

        ResponseEntity<ResponseObject> responseEntity = imageController.deleteImages(ids);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete image successfully", responseEntity.getBody().getMessage());
    }
}