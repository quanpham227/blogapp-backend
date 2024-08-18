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
import java.util.Map;
@Service
@RequiredArgsConstructor
public class CloudinaryService  implements ICloudinaryService{
    private final Cloudinary cloudinary;
    @Override
    public CloudinaryDTO upload(MultipartFile file, String folderName) {
        try {
            Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "blogapp/" + folderName, "resource_type", "auto"));
            CloudinaryDTO cloudinaryDTO = new CloudinaryDTO();
            cloudinaryDTO.setPublicId(uploadResult.get("public_id").toString());
            cloudinaryDTO.setUrl((String) uploadResult.get("secure_url"));
            return cloudinaryDTO;

        } catch (Exception ex) {
            throw new CloudinaryException("failed to load to Cloudinary the image file: ", ex);
        }
    }

    @Override
    public CloudinaryDTO update(String publicId, MultipartFile file) {
        try {
            Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", publicId, "resource_type", "auto"));
            CloudinaryDTO cloudinaryDTO = new CloudinaryDTO();
            cloudinaryDTO.setPublicId(uploadResult.get("public_id").toString());
            cloudinaryDTO.setUrl((String) uploadResult.get("secure_url"));
            return cloudinaryDTO;
        }catch (Exception exception){
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

}
