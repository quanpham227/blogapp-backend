package com.pivinadanang.blog.services.image;

import com.pivinadanang.blog.responses.image.ImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Consumer;

public interface IImageService {


    List<ImageResponse> uploadImages(String objectType, List<MultipartFile> files) throws Exception;
    ImageResponse uploadImage(String objectType, MultipartFile file) throws Exception;
    ImageResponse getImage(long id) throws Exception;

    Page<ImageResponse> getAllImages(String keyword , String objectType, Pageable pageable);

    void deleteImages(List<Long> ids) throws Exception;

    Long getTotalFileSize();


    Page<ImageResponse> getUnusedImages(Pageable pageable);
}
