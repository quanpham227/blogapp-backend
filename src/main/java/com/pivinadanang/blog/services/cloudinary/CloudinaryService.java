package com.pivinadanang.blog.services.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.pivinadanang.blog.dtos.CloudinaryDTO;
import com.pivinadanang.blog.exceptions.CloudinaryException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class CloudinaryService  implements ICloudinaryService{
    private final Cloudinary cloudinary;
    @Override
    public CloudinaryDTO upload(MultipartFile file, String folderName, int width, int height) {
        try {
            // Create Transformation to resize the image
            Transformation transformation = new Transformation()
                    .width(width)
                    .height(height)
                    .crop("scale"); // Crop to ensure the image has the exact size
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "blogapp/" + folderName,
                            "resource_type", "auto",
                            "transformation", transformation
                    ));

            // Create a new map with the correct type
            Map<String, Object> typedUploadResult = new HashMap<>();
            for (Map.Entry<?, ?> entry : uploadResult.entrySet()) {
                typedUploadResult.put((String) entry.getKey(), entry.getValue());
            }

            return mapUploadResultToDTO(typedUploadResult, file.getOriginalFilename());

        } catch (Exception ex) {
            throw new CloudinaryException("failed to load to Cloudinary the image file: ", ex);
        }
    }
    @Override
    public CloudinaryDTO update(String publicId, MultipartFile file, int width, int height) {
        try {
            // Thiết lập các tham số chuyển đổi
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "auto",
                    "transformation", new Transformation().width(width).height(height).crop("fit")
            );

            // Upload ảnh với các tham số chuyển đổi
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            // Tạo đối tượng CloudinaryDTO từ kết quả upload
            CloudinaryDTO cloudinaryDTO = new CloudinaryDTO();
            cloudinaryDTO.setPublicId(uploadResult.get("public_id").toString());
            cloudinaryDTO.setUrl((String) uploadResult.get("secure_url"));
            cloudinaryDTO.setFileName(file.getOriginalFilename());
            cloudinaryDTO.setFileSize(Long.parseLong(uploadResult.get("bytes").toString())); // Kích thước file từ Cloudinary
            return cloudinaryDTO;
        } catch (Exception exception) {
            throw new CloudinaryException("Failed to update image on Cloudinary with public ID " + publicId, exception);
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            Map<?,?> destroy = this.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception ex) {
            throw new CloudinaryException("failed to delete  Cloudinary the image file: ");

        }
    }

    @Override
    public ByteArrayResource download(String publicId, int width, int height, boolean isAvatar) {
        String format = "jpg";
        Transformation transformation = new Transformation().width(width).height(height).crop("fill");
        if (isAvatar) {
            transformation = transformation.radius("max");
            format = "png";
        }
        String cloudUrl = cloudinary.url().secure(true).format(format)
                .transformation(transformation)
                .publicId(publicId)
                .generate();
        try {
            URL url = new URL(cloudUrl);
            InputStream inputStream = url.openStream();
            byte[] out = IOUtils.toByteArray(inputStream);
            ByteArrayResource resource = new ByteArrayResource(out);
            return resource;
        } catch (Exception ex) {
            throw new CloudinaryException("failed to download image from Cloudinary with public ID " + publicId, ex);
        }
    }
    private CloudinaryDTO mapUploadResultToDTO(Map<String, Object> uploadResult, String originalFileName) {
        CloudinaryDTO cloudinaryDTO = new CloudinaryDTO();
        cloudinaryDTO.setPublicId(uploadResult.get("public_id") != null ? uploadResult.get("public_id").toString() : null);
        cloudinaryDTO.setUrl(uploadResult.get("secure_url") != null ? uploadResult.get("secure_url").toString() : null);
        cloudinaryDTO.setFileName(originalFileName);
        cloudinaryDTO.setFileSize(uploadResult.get("bytes") != null ? Long.parseLong(uploadResult.get("bytes").toString()) : 0L);
        return cloudinaryDTO;
    }
    // Phương thức kiểm tra sự tồn tại của hình ảnh trên Cloudinary
    public boolean checkImageExistsOnCloudinary(String publicId) {
        try {
            Map<?, ?> result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            return result != null && result.get("public_id") != null;
        } catch (Exception e) {
            // Hình ảnh không tồn tại hoặc có lỗi xảy ra
            return false;
        }
    }

    // Lấy danh sách tài nguyên trong một thư mục cụ thể
    @Override
    public Map<?, ?> listResources(String folderName) {
        try {
            return cloudinary.api().resources(ObjectUtils.asMap("type", "upload", "prefix", folderName));
        } catch (Exception e) {
            throw new CloudinaryException("Failed to list resources in folder: " + folderName, e);
        }
    }
    // Di chuyển hoặc đổi tên một tài nguyên
    @Override
    public void renameResource(String oldPublicId, String newPublicId) {
        try {
            cloudinary.uploader().rename(oldPublicId, newPublicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new CloudinaryException("Failed to rename resource from " + oldPublicId + " to " + newPublicId, e);
        }
    }
    // Tạo một thư mục mới
    @Override
    public void createFolder(String folderName) {
        try {
            cloudinary.api().createFolder(folderName, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new CloudinaryException("Failed to create folder: " + folderName, e);
        }
    }
    // Xóa một thư mục cụ thể
    @Override
    public void deleteFolder(String folderName) {
        try {
            cloudinary.api().deleteFolder(folderName, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new CloudinaryException("Failed to delete folder: " + folderName, e);
        }
    }
}
