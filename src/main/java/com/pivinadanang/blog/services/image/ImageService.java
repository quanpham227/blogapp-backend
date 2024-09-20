package com.pivinadanang.blog.services.image;

import com.pivinadanang.blog.components.ImageSizeConfig;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.CloudinaryDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.FileNotFoundException;
import com.pivinadanang.blog.exceptions.FileSizeExceededException;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.repositories.ImageRepository;
import com.pivinadanang.blog.responses.image.ImageResponse;
import com.pivinadanang.blog.services.cloudinary.CloudinaryService;
import com.pivinadanang.blog.ultils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.pivinadanang.blog.ultils.MessageKeys;
import com.pivinadanang.blog.exceptions.UnsupportedMediaTypeException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;
    private final LocalizationUtils localizationUtils;
    private final ImageSizeConfig imageSizeConfig; // Inject the configuration class



    @Override
    @Transactional
    public List<ImageResponse> uploadImages(String objectType, List<MultipartFile> files) throws Exception {
        List<ImageResponse> imageResponses = new ArrayList<>(); // Khởi tạo danh sách phản hồi
        int[] dimensions = imageSizeConfig.getSizeConfig(objectType); // Giá trị mặc định nếu không có trong Map
        int width = dimensions[0];
        int height = dimensions[1];
        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue; // Bỏ qua các file trống
            }
            // Kiểm tra kích thước file và định dạng
            if (file.getSize() > 5 * 1024 * 1024) { // Kích thước > 5MB
                throw new FileSizeExceededException(
                        localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new UnsupportedMediaTypeException(
                        localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
            }
            File tempFile = FileUtils.handleFile(file);
            // Upload file lên Cloudinary và lưu thông tin ảnh vào cơ sở dữ liệu
            CloudinaryDTO cloudinaryDTO = cloudinaryService.upload(file, objectType, width, height);
            ImageEntity imageEntity = ImageEntity.builder()
                    .imageUrl(cloudinaryDTO.getUrl())
                    .fileName(cloudinaryDTO.getFileName())
                    .publicId(cloudinaryDTO.getPublicId())
                    .objectType(objectType)
                    .fileType(file.getContentType())
                    .fileSize(cloudinaryDTO.getFileSize())
                    .build();

            ImageEntity savedImage = imageRepository.save(imageEntity);

            // Tạo đối tượng phản hồi và thêm vào danh sách
            ImageResponse imageResponse = ImageResponse.fromImage(savedImage);

            imageResponses.add(imageResponse);
        }
        return imageResponses;
    }

    @Override
    @Transactional
    public ImageResponse uploadImage(String objectType, MultipartFile file) throws Exception {
        int[] dimensions = imageSizeConfig.getSizeConfig(objectType); // Giá trị mặc định nếu không có trong Map
        int width = dimensions[0];
        int height = dimensions[1];
        if (file.getSize() == 0  || file.isEmpty()) {
            throw new FileNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGE_FILE_NOTFOUND));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new UnsupportedMediaTypeException(
                    localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
        }
        File tempFile = FileUtils.handleFile(file);
        // Upload file lên Cloudinary và lưu thông tin ảnh vào cơ sở dữ liệu
        CloudinaryDTO cloudinaryDTO = cloudinaryService.upload(file, objectType, width, height);
        ImageEntity imageEntity = ImageEntity.builder()
                .imageUrl(cloudinaryDTO.getUrl())
                .fileName(cloudinaryDTO.getFileName())
                .publicId(cloudinaryDTO.getPublicId())
                .objectType(objectType)
                .fileType(file.getContentType())
                .fileSize(cloudinaryDTO.getFileSize())
                .isUsed(false)
                .usageCount(0)
                .build();

        ImageEntity savedImage = imageRepository.save(imageEntity);

        // Tạo đối tượng phản hồi và thêm vào danh sách
        ImageResponse imageResponse = ImageResponse.fromImage(savedImage);

        return imageResponse;
    }

    @Override
    public ImageResponse getImage(long id) throws Exception {
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.IMAGE_NOT_FOUND)));
        return ImageResponse.fromImage(imageEntity);
    }

    @Override
    public Page<ImageResponse> getAllImages(String keyword,String objectType ,PageRequest pageRequest) {
        Page<ImageEntity> imagePage;
        imagePage = imageRepository.searchImages(keyword,objectType ,pageRequest);
        return imagePage.map(ImageResponse::fromImage);
    }

    @Override
    public Long getTotalFileSize() {
        return imageRepository.getTotalFileSize();
    }

    @Override
    @Transactional
    public void deleteImages(List<Long> ids) throws Exception {
        for (Long id : ids) {
            ImageEntity imageEntity = imageRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException(
                            localizationUtils.getLocalizedMessage(MessageKeys.DELETE_IMAGE_FAILED, id)));
            try {
                imageRepository.delete(imageEntity);
                cloudinaryService.delete(imageEntity.getPublicId());
            } catch (Exception e) {
                throw new Exception(
                        localizationUtils.getLocalizedMessage(MessageKeys.DELETE_IMAGE_FAILED, id), e);
            }
        }
    }
}
