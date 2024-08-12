package com.pivinadanang.blog.controller;

import com.github.javafaker.Faker;
import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.GoogleDriveDTO;
import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.PostImageDTO;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.models.PostImageContent;
import com.pivinadanang.blog.models.PostImageEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.post.PostListResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.google.IGoogleService;
import com.pivinadanang.blog.services.image.IPostImageContentService;
import com.pivinadanang.blog.services.post.IPostService;
import com.pivinadanang.blog.ultils.FileUtils;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${api.prefix}/posts")
@Validated
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;
    private final IGoogleService googleService;
    private final LocalizationUtils localizationUtils;
    private final IPostImageContentService postImageContentService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> createPost(@Valid @ModelAttribute PostDTO postDTO,
                                                     @RequestParam ("category_id") Long categoryId,
                                                     @RequestParam ("thumbnail") MultipartFile file,
                                                     BindingResult result){
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Validation errors")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errorMessages)
                            .build());
        }
        if(postService.existsPostByTitle(postDTO.getTitle())){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        // Kiểm tra kích thước file và định dạng
        if(file.getSize() > 5 * 1024 * 1024) { // Kích thước > 10MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(ResponseObject.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                            .status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .build());
        }
        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ResponseObject.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
                            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .build());
        }
        try {
            postDTO.setCategoryId(categoryId);
            File tempFile = FileUtils.handleFile(file);
            GoogleDriveDTO googleDriveDTO = googleService.uploadImageToDrive(tempFile);
            PostImageDTO postImageDTO =  PostImageDTO.builder()
                    .imageUrl(googleDriveDTO.getUrl())
                    .fileId(googleDriveDTO.getFileId())
                    .build();
            postDTO.setPostImage(postImageDTO);
            PostResponse postResponse = postService.createPost(postDTO);
            return ResponseEntity.ok(ResponseObject.builder()
                            .status(HttpStatus.CREATED)
                            .data(postResponse)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_SUCCESSFULLY))
                    .build());
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_FAILED))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseObject> getPostById(@PathVariable Long id) throws Exception {
        PostEntity existingPost = postService.getPostById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(PostResponse.fromPost(existingPost))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY))
                .build());
    }
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> updatePost(@Valid @ModelAttribute PostDTO postDTO,
                                                     @PathVariable Long id,
                                                     @RequestParam (value = "category_id", required = false) Long categoryId,
                                                     @RequestParam (value = "thumbnail", required = false) MultipartFile file,
                                                     BindingResult result){
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Validation errors")
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errorMessages)
                            .build());
        }
        if(postService.existsPostByTitle(postDTO.getTitle())){
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_POST_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        try {
            PostImageDTO postImageDTO = null;
            if(file != null && !file.isEmpty()){
                if(file.getSize() > 5 * 1024 * 1024) { // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(ResponseObject.builder()
                                    .message(localizationUtils
                                            .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE))
                                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                                    .build());
                }
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(ResponseObject.builder()
                                    .message(localizationUtils
                                            .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE))
                                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                    .build());
                }
                File tempFile = FileUtils.handleFile(file);
                GoogleDriveDTO googleDriveDTO = googleService.uploadImageToDrive(tempFile);
                 postImageDTO =  PostImageDTO.builder()
                        .imageUrl(googleDriveDTO.getUrl())
                        .fileId(googleDriveDTO.getFileId())
                        .build();
                postDTO.setPostImage(postImageDTO);
            }
            postDTO.setCategoryId(categoryId);
            PostResponse postResponse = postService.updatePost(id,postDTO);
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_POST_SUCCESSFULLY))
                            .status(HttpStatus.CREATED)
                            .data(postResponse)
                            .build());
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("An error occurred: " + exception.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deletePost(@PathVariable Long id) throws Exception{
            postService.deletePost(id);
            return ResponseEntity.ok(ResponseObject.builder()
                            .data(null)
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_POST_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                    .build());
    }

    @GetMapping("")
    public ResponseEntity<PostListResponse> getPosts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit)  {
        //productRedisService.clear();
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("createdAt").descending()
        );
        Page<PostResponse> postPage = postService.getAllPosts( keyword, categoryId, pageRequest);
        // Lấy tổng số trang
        int totalPages = postPage.getTotalPages();
        List<PostResponse> posts = postPage.getContent();


        return ResponseEntity.ok(PostListResponse
                .builder()
                .posts(posts)
                .totalPages(totalPages)
                .build());

    }

    @GetMapping("/recent")
    public ResponseEntity<PostListResponse> getRecentPosts(@RequestParam(defaultValue = "5") int limit) {
        List<PostResponse> recentPosts = postService.getRecentPosts(limit);

        return ResponseEntity.ok(PostListResponse
                .builder()
                .posts(recentPosts)
                .build());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseObject> getPostBySlug(@PathVariable String slug) throws Exception {
        PostEntity postEntity = postService.getPostBySlug(slug);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(PostResponse.fromPost(postEntity))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY))
                .build());
    }


    @PostMapping("/generateFakePosts")
    public ResponseEntity<String> generateFakePosts(){
        Faker faker = new Faker(new Locale("vi"));
        for(int i = 0; i < 1000; i++){
            String title = faker.book().title() + " - " + faker.book().title() + " - " + faker.book().title();
            if(postService.existsPostByTitle(title)){
                continue;
            }
            String content = faker.lorem().paragraphs(3).toString();
            Long categoryId = (long) faker.number().numberBetween(1,5);
            String[] imageUrls = {
                   "https://drive.google.com/thumbnail?id=1Xj4C6eKfVL1krZ6ylNvhXFIp8X2Xzx5e&sz=w4000",
                    "https://drive.google.com/thumbnail?id=15He3o1pAAVGujxlHBc7ef2YK-4moa9if&sz=w4000",
                    "https://drive.google.com/thumbnail?id=139rjuisL9A1XP0blu1hXgkmyfqDh5suE&sz=w4000",
                    "https://drive.google.com/thumbnail?id=1PtPUaw92MJF1Ph_zBYgeoW3nCKo_JKjP&sz=w4000",
                    "https://drive.google.com/thumbnail?id=1KMxf6wpWO4ry_DtWkWvs-IgimycOmDQv&sz=w4000"
            };
            String imageUrl = imageUrls[faker.random().nextInt(imageUrls.length)];
            String fileId = "1Xj4C6eKfVL1krZ6ylNvhXFIp8X2Xzx5e";
            PostImageDTO postImage = PostImageDTO.builder()
                    .imageUrl(imageUrl)
                    .fileId(fileId)
                    .build();
            PostDTO postDTO = PostDTO.builder()
                    .title(title)
                    .content(content)
                    .categoryId(categoryId)
                    .postImage(postImage)
                    .build();
            try {
                postService.createPost(postDTO);
            }catch (Exception exception){
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }
        return ResponseEntity.ok("Posts generated successfully");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Please select a file to upload")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(ResponseObject.builder()
                            .message("File size must be less than 5MB")
                            .status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .build());
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ResponseObject.builder()
                            .message("File must be an image")
                            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .build());
        }
        try {
            File tempFile = FileUtils.handleFile(file);
            GoogleDriveDTO googleDriveDTO = googleService.uploadImageToDrive(tempFile);
            PostImageContent postImageContent = PostImageContent.builder()
                    .imageUrl(googleDriveDTO.getUrl())
                    .fileId(googleDriveDTO.getFileId())
                    .build();

            PostImageContent postImageContentResponse =  postImageContentService.createPostImageContent(postImageContent);

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.CREATED)
                    .data(postImageContentResponse)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_SUCCESSFULLY))
                    .build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Failed to upload image")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

}
