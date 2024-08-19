package com.pivinadanang.blog.services.image;

import com.pivinadanang.blog.responses.image.ImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {


    List<ImageResponse> uploadImages(String objectType, List<MultipartFile> files) throws Exception;
    ImageResponse uploadImage(String objectType, MultipartFile file) throws Exception;

    Page<ImageResponse> getAllImages(String keyword , PageRequest pageRequest);

    void deleteImage(long id) throws Exception;

}
