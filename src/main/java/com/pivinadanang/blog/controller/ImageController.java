package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.image.ImageResponse;
import com.pivinadanang.blog.services.image.IImageService;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/uploads")
@RequiredArgsConstructor
public class ImageController {
    private final LocalizationUtils localizationUtils;
    private final IImageService fileUploadService;

    @PostMapping(value = "multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> uploadImages(
            @RequestParam("object_type") String objectType,
            @RequestParam("files") List<MultipartFile> files) throws Exception {

        files = files == null ? new ArrayList<>() : files;
        // Kiểm tra số lượng file
        if (files.size() > ImageEntity.MAXIMUM_IMAGES) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5))
                            .build()
            );
        }
        // Gọi service để upload file
        List<ImageResponse> images = fileUploadService.uploadImages(objectType, files);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(images)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY))
                .build());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "single",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> uploadImage(@RequestParam("object_type") String objectType,
                                                        @RequestParam("file") MultipartFile file) throws Exception {
        // Gọi service để upload file
        ImageResponse image = fileUploadService.uploadImage(objectType, file);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(image)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY))
                .build());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteImage(@PathVariable Long id) throws Exception{
        fileUploadService.deleteImage(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY))
                        .build());
    }
}
