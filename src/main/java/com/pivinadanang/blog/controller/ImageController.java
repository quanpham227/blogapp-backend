package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.comment.CommentResponse;
import com.pivinadanang.blog.responses.image.ImageListResponse;
import com.pivinadanang.blog.responses.image.ImageResponse;
import com.pivinadanang.blog.services.image.IImageService;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/images")
@RequiredArgsConstructor
public class ImageController {
    private final LocalizationUtils localizationUtils;
    private final IImageService fileUploadService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getImages(@RequestParam(defaultValue = "") String keyword,
                                                    @RequestParam(defaultValue = "", name = "object_type") String objectType,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "24") int limit) {

        if (page < 0 || limit <= 0) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Invalid page or limit")
                            .build()
            );
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        try {
            Page<ImageResponse> imagePage;
            if ("unused".equals(objectType)) {
                imagePage = fileUploadService.getUnusedImages(pageable);
            } else {
                imagePage = fileUploadService.getAllImages(keyword, objectType, pageable);
            }

            List<ImageResponse> images = imagePage != null ? imagePage.getContent() : new ArrayList<>();
            int totalPages = imagePage != null ? imagePage.getTotalPages() : 0;
            Long totalFileSize = fileUploadService.getTotalFileSize();
            if (totalFileSize == null) {
                totalFileSize = 0L;
            }

            ImageListResponse imageListResponse = ImageListResponse.builder()
                    .images(images)
                    .totalPages(totalPages)
                    .status(HttpStatus.OK)
                    .totalFileSizes(totalFileSize)
                    .build();

            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Get images successfully")
                    .status(HttpStatus.OK)
                    .data(imageListResponse)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    public ResponseEntity<ResponseObject> getImage(Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Invalid image ID")
                            .build()
            );
        }
        try {
            ImageResponse imageResponse = fileUploadService.getImage(id);
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(imageResponse)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_IMAGE_SUCCESSFULLY))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .build()
            );
        }
    }


    @PostMapping(value = "upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> uploadImages(
            @RequestParam("object_type") String objectType,
            @RequestParam("files") List<MultipartFile> files) throws Exception {

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("No files to upload")
                            .build()
            );
        }

        // Check the number of files
        if (files.size() > ImageEntity.MAXIMUM_IMAGES) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Upload images exceed the maximum limit")
                            .build()
            );
        }

        for (MultipartFile file : files) {
            if (!isValidFileFormat(file.getContentType())) {
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .message("Invalid file format")
                                .build()
                );
            }
        }

        try {
            // Call service to upload files
            List<ImageResponse> imageResponses = fileUploadService.uploadImages(objectType, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(imageResponses)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY))
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message("Service error")
                            .build()
            );
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping(value = "upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> uploadImage(@RequestParam("object_type") String objectType,
                                                      @RequestParam("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Invalid file")
                            .build()
            );
        }

        if (!isValidFileFormat(file.getContentType())) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Invalid file format")
                            .build()
            );
        }

        try {
            // Call service to upload file
            ImageResponse image = fileUploadService.uploadImage(objectType, file);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(image)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY))
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message("Service error")
                            .build()
            );
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteImages(@RequestBody List<Long> ids) throws Exception {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Invalid image IDs")
                            .build()
            );
        }

        try {
            fileUploadService.deleteImages(ids);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message("Service error")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .message("Image not found")
                            .build()
            );
        }

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_IMAGE_SUCCESSFULLY, ids))
                        .build()
        );
    }
    private boolean isValidFileFormat(String contentType) {
        // Add your logic to validate the file format
        return "image/jpeg".equals(contentType) || "image/png".equals(contentType);
    }
}
