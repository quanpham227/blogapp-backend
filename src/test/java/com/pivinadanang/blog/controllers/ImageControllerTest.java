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
    public void testGetImages_Success_NoKeyword() {
        Pageable pageable = PageRequest.of(0, 24, Sort.by("createdAt").descending());
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse());
        Page<ImageResponse> imagePage = new PageImpl<>(imageResponses, pageable, imageResponses.size());
        Long totalFileSize = 100L;

        when(fileUploadService.getAllImages("", "", pageable)).thenReturn(imagePage);
        when(fileUploadService.getTotalFileSize()).thenReturn(totalFileSize);

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("", "", 0, 24);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get images successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponses, ((ImageListResponse) responseEntity.getBody().getData()).getImages());
        assertEquals(totalFileSize, ((ImageListResponse) responseEntity.getBody().getData()).getTotalFileSizes());
    }

    @Test
    public void testGetImages_Success_WithKeyword() {
        Pageable pageable = PageRequest.of(0, 24, Sort.by("createdAt").descending());
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse());
        Page<ImageResponse> imagePage = new PageImpl<>(imageResponses, pageable, imageResponses.size());
        Long totalFileSize = 100L;

        when(fileUploadService.getAllImages("test", "", pageable)).thenReturn(imagePage);
        when(fileUploadService.getTotalFileSize()).thenReturn(totalFileSize);

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("test", "", 0, 24);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get images successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponses, ((ImageListResponse) responseEntity.getBody().getData()).getImages());
        assertEquals(totalFileSize, ((ImageListResponse) responseEntity.getBody().getData()).getTotalFileSizes());
    }

    @Test
    public void testGetImages_Success_UnusedObjectType() {
        Pageable pageable = PageRequest.of(0, 24, Sort.by("createdAt").descending());
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse());
        Page<ImageResponse> imagePage = new PageImpl<>(imageResponses, pageable, imageResponses.size());
        Long totalFileSize = 100L;

        when(fileUploadService.getUnusedImages(pageable)).thenReturn(imagePage);
        when(fileUploadService.getTotalFileSize()).thenReturn(totalFileSize);

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("", "unused", 0, 24);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get images successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponses, ((ImageListResponse) responseEntity.getBody().getData()).getImages());
        assertEquals(totalFileSize, ((ImageListResponse) responseEntity.getBody().getData()).getTotalFileSizes());
    }

    @Test
    public void testGetImages_Success_WithPagination() {
        Pageable pageable = PageRequest.of(1, 10, Sort.by("createdAt").descending());
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse());
        Page<ImageResponse> imagePage = new PageImpl<>(imageResponses, pageable, imageResponses.size());
        Long totalFileSize = 100L;

        when(fileUploadService.getAllImages("", "", pageable)).thenReturn(imagePage);
        when(fileUploadService.getTotalFileSize()).thenReturn(totalFileSize);

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("", "", 1, 10);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get images successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponses, ((ImageListResponse) responseEntity.getBody().getData()).getImages());
        assertEquals(totalFileSize, ((ImageListResponse) responseEntity.getBody().getData()).getTotalFileSizes());
    }

    @Test
    public void testGetImages_Failure_InvalidPageAndLimit() {
        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("", "", -1, -1);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid page or limit", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetImages_Failure_NoImagesFound() {
        Pageable pageable = PageRequest.of(0, 24, Sort.by("createdAt").descending());
        Page<ImageResponse> imagePage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Long totalFileSize = 0L;

        when(fileUploadService.getAllImages("", "", pageable)).thenReturn(imagePage);
        when(fileUploadService.getTotalFileSize()).thenReturn(totalFileSize);

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("", "", 0, 24);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Get images successfully", responseEntity.getBody().getMessage());
        assertEquals(Collections.emptyList(), ((ImageListResponse) responseEntity.getBody().getData()).getImages());
        assertEquals(totalFileSize, ((ImageListResponse) responseEntity.getBody().getData()).getTotalFileSizes());
    }

    @Test
    public void testGetImage_Success() throws Exception {
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
    public void testGetImage_Failure_IdNotFound() throws Exception {
        Long imageId = 1L;

        when(fileUploadService.getImage(imageId)).thenThrow(new Exception("Image not found"));

        ResponseEntity<ResponseObject> responseEntity = imageController.getImage(imageId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Image not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testGetImage_Failure_InvalidId() throws Exception {
        ResponseEntity<ResponseObject> responseEntity = imageController.getImage(null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid image ID", responseEntity.getBody().getMessage());
    }
    @Test
    public void testUploadImages_Success() throws Exception {
        List<MultipartFile> files = Arrays.asList(mock(MultipartFile.class), mock(MultipartFile.class));
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse());

        when(fileUploadService.uploadImages("objectType", files)).thenReturn(imageResponses);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY)).thenReturn("Upload image successfully");

        for (MultipartFile file : files) {
            when(file.getContentType()).thenReturn("image/jpeg");
        }

        ResponseEntity<?> responseEntity = imageController.uploadImages("objectType", files);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Upload image successfully", ((ResponseObject) responseEntity.getBody()).getMessage());
        assertEquals(imageResponses, ((ResponseObject) responseEntity.getBody()).getData());
    }


    @Test
    public void testUploadImages_Success_MaxImages() throws Exception {
        List<MultipartFile> files = Arrays.asList(mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class));
        List<ImageResponse> imageResponses = Arrays.asList(new ImageResponse(), new ImageResponse(), new ImageResponse(), new ImageResponse(), new ImageResponse());

        when(fileUploadService.uploadImages("objectType", files)).thenReturn(imageResponses);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY)).thenReturn("Upload image successfully");

        for (MultipartFile file : files) {
            when(file.getContentType()).thenReturn("image/jpeg");
        }

        ResponseEntity<?> responseEntity = imageController.uploadImages("objectType", files);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Upload image successfully", ((ResponseObject) responseEntity.getBody()).getMessage());
        assertEquals(imageResponses, ((ResponseObject) responseEntity.getBody()).getData());
    }

    @Test
    public void testUploadImages_Failure_ExceedMaxImages() throws Exception {
        List<MultipartFile> files = Arrays.asList(mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class), mock(MultipartFile.class));

        ResponseEntity<?> responseEntity = imageController.uploadImages("objectType", files);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Upload images exceed the maximum limit", ((ResponseObject) responseEntity.getBody()).getMessage());
    }

    @Test
    public void testUploadImages_Failure_NullOrEmptyFiles() throws Exception {
        ResponseEntity<?> responseEntity = imageController.uploadImages("objectType", null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("No files to upload", ((ResponseObject) responseEntity.getBody()).getMessage());
    }

    @Test
    public void testUploadImages_Failure_InvalidFileFormat() throws Exception {
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getContentType()).thenReturn("invalid/type");

        List<MultipartFile> files = Collections.singletonList(invalidFile);

        ResponseEntity<?> responseEntity = imageController.uploadImages("objectType", files);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid file format", ((ResponseObject) responseEntity.getBody()).getMessage());
    }

    @Test
    public void testUploadImages_Failure_Unauthorized() throws Exception {
        // Simulate unauthorized access
        // This test case requires a security context setup to simulate unauthorized access
    }
    @Test
    public void testUploadImage_Success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        ImageResponse imageResponse = new ImageResponse();

        when(file.getContentType()).thenReturn("image/jpeg");
        when(fileUploadService.uploadImage("objectType", file)).thenReturn(imageResponse);
        when(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY)).thenReturn("Upload image successfully");

        ResponseEntity<ResponseObject> responseEntity = imageController.uploadImage("objectType", file);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Upload image successfully", responseEntity.getBody().getMessage());
        assertEquals(imageResponse, responseEntity.getBody().getData());
    }

    @Test
    public void testUploadImage_Failure_NullOrInvalidFile() throws Exception {
        ResponseEntity<ResponseObject> responseEntity = imageController.uploadImage("objectType", null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid file", responseEntity.getBody().getMessage());
    }

    @Test
    public void testUploadImage_Failure_InvalidFileFormat() throws Exception {
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getContentType()).thenReturn("invalid/type");

        ResponseEntity<ResponseObject> responseEntity = imageController.uploadImage("objectType", invalidFile);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid file format", responseEntity.getBody().getMessage());
    }

    @Test
    public void testUploadImage_Failure_Unauthorized() throws Exception {
        // Simulate unauthorized access
        // This test case requires a security context setup to simulate unauthorized access
    }
    @Test
    public void testDeleteImages_Success() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);

        doNothing().when(fileUploadService).deleteImages(ids);
        when(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_IMAGE_SUCCESSFULLY, ids)).thenReturn("Delete image successfully");

        ResponseEntity<ResponseObject> responseEntity = imageController.deleteImages(ids);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delete image successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void testDeleteImages_Failure_EmptyOrNullIds() throws Exception {
        ResponseEntity<ResponseObject> responseEntity = imageController.deleteImages(null);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid image IDs", responseEntity.getBody().getMessage());
    }

    @Test
    public void testDeleteImages_Failure_IdNotFound() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);

        doThrow(new Exception("Image not found")).when(fileUploadService).deleteImages(ids);

        ResponseEntity<ResponseObject> responseEntity = imageController.deleteImages(ids);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Image not found", responseEntity.getBody().getMessage());
    }

    @Test
    public void testDeleteImages_Failure_Unauthorized() throws Exception {
        // Simulate unauthorized access
        // This test case requires a security context setup to simulate unauthorized access
    }
    @Test
    public void testAuthorization_Success_AdminOrModerator() throws Exception {
        // Simulate authorized access for ADMIN or MODERATOR
        // This test case requires a security context setup to simulate authorized access
    }

    @Test
    public void testAuthorization_Failure_Forbidden() throws Exception {
        // Simulate unauthorized access
        // This test case requires a security context setup to simulate unauthorized access
    }
    @Test
    public void testServiceError_GetUnusedImages() throws Exception {
        Pageable pageable = PageRequest.of(0, 24, Sort.by("createdAt").descending());

        when(fileUploadService.getUnusedImages(pageable)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("", "unused", 0, 24);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }

    @Test
    public void testServiceError_GetAllImages() throws Exception {
        Pageable pageable = PageRequest.of(0, 24, Sort.by("createdAt").descending());

        when(fileUploadService.getAllImages("", "", pageable)).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<ResponseObject> responseEntity = imageController.getImages("", "", 0, 24);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }



    @Test
    public void testServiceError_DeleteImages() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);

        doThrow(new RuntimeException("Service error")).when(fileUploadService).deleteImages(ids);

        ResponseEntity<ResponseObject> responseEntity = imageController.deleteImages(ids);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Service error", responseEntity.getBody().getMessage());
    }
}
