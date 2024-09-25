package com.pivinadanang.blog.services.cloudinary;

import com.pivinadanang.blog.dtos.CloudinaryDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ICloudinaryService {

    CloudinaryDTO upload(MultipartFile file, String folderName, int width, int height);

    CloudinaryDTO update(String publicId, MultipartFile file, int width, int height);

    void delete(String publicId);

    ByteArrayResource download(String publicId, int width, int height, boolean isAvatar);

    boolean checkImageExistsOnCloudinary(String publicId);

    Map<?, ?> listResources(String folderName);

    void renameResource(String oldPublicId, String newPublicId);

    void createFolder(String folderName);

    void deleteFolder(String folderName);
}

