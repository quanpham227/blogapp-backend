package com.pivinadanang.blog.controller;

import com.github.javafaker.Faker;
import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.GoogleDriveDTO;
import com.pivinadanang.blog.dtos.PostDTO;
import com.pivinadanang.blog.dtos.PostImageDTO;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.post.PostListResponse;
import com.pivinadanang.blog.responses.post.PostResponse;
import com.pivinadanang.blog.services.google.IGoogleService;
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
import java.util.List;

@RestController
@RequestMapping("api/v1/posts")
@Validated
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;
    private final IGoogleService googleService;
    private final LocalizationUtils localizationUtils;


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
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
        try {
            postDTO.setCategoryId(categoryId);
            //handleFile
            File tempFile = FileUtils.handleFile(file);
            // Upload file lên Google Drive
            GoogleDriveDTO googleDriveDTO = googleService.uploadImageToDrive(tempFile);
            // Xoá file tạm sau khi upload
            tempFile.delete();
            // Lưu thông tin bài viết (bao gồm URL ảnh từ Google Drive)
            PostImageDTO postImageDTO =  PostImageDTO.builder()
                    .imageUrl(googleDriveDTO.getUrl())
                    .fileId(googleDriveDTO.getFileId())
                    .build();
            postDTO.setPostImage(postImageDTO);

            PostResponse postResponse = postService.createPost(postDTO);
            // Add logging here to inspect the postEntity
            System.out.println("PostEntity: " + postResponse);

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
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getPostById(@PathVariable Long id) throws Exception {
        PostEntity existingPost = postService.getPostById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(PostResponse.fromPost(existingPost))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_POST_SUCCESSFULLY))
                .build());
    }
    @PatchMapping("/{id}")
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
        try {
            PostImageDTO postImageDTO = null;
            if(file != null && !file.isEmpty()){
                File tempFile = FileUtils.handleFile(file);
                GoogleDriveDTO googleDriveDTO = googleService.uploadImageToDrive(tempFile);
                tempFile.delete();
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
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        Page<PostResponse> postPage = postService.getAllPosts( pageRequest);
        int totalPages = postPage.getTotalPages();
        List<PostResponse> posts = postPage.getContent();
        return ResponseEntity.ok(PostListResponse
                .builder()
                .posts(posts)
                .totalPages(totalPages)
                .build());

    }

    @PostMapping("/generateFakePosts")
    public ResponseEntity<String> generateFakePosts(){
        Faker faker = new Faker();
        for(int i = 0; i < 100000; i++){
            String title = faker.book().title() + " - " + faker.book().title();
            if(postService.existsPostByTitle(title)){
                continue;
            }
            String content = faker.lorem().paragraphs(3).toString();
            Long categoryId = (long) faker.number().numberBetween(1,5);
            String[] imageUrls = {
                    "https://drive.google.com/uc?export=view&id=1Xj4C6eKfVL1krZ6ylNvhXFIp8X2Xzx5e",
                    "https://drive.google.com/uc?export=view&id=15He3o1pAAVGujxlHBc7ef2YK-4moa9if",
                    "https://drive.google.com/uc?export=view&id=139rjuisL9A1XP0blu1hXgkmyfqDh5suE",
                    "https://drive.google.com/uc?export=view&id=1PtPUaw92MJF1Ph_zBYgeoW3nCKo_JKjP",
                    "https://drive.google.com/uc?export=view&id=1KMxf6wpWO4ry_DtWkWvs-IgimycOmDQv"
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
            postDTO.generateSlug();
            try {
                postService.createPost(postDTO);
            }catch (Exception exception){
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }
        return ResponseEntity.ok("Posts generated successfully");
    }
}
