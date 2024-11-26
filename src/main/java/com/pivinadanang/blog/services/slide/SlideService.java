package com.pivinadanang.blog.services.slide;

import com.pivinadanang.blog.dtos.SlideDTO;
import com.pivinadanang.blog.dtos.SlideOrderDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.models.SlideEntity;
import com.pivinadanang.blog.repositories.ImageRepository;
import com.pivinadanang.blog.repositories.SlideRepository;
import com.pivinadanang.blog.responses.slide.SlideResponse;
import com.pivinadanang.blog.ultils.HtmlSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlideService implements ISlideService{
    private final SlideRepository slideRepository;
    private final ImageRepository imageRepository;

    @Override
    public boolean existsByTitle(String title) {
        return slideRepository.existsByTitle(title);
    }

    @Override
    public SlideResponse findById(long id) throws Exception {
        SlideEntity  slide =  slideRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find slide with id " + id));
        return SlideResponse.fromSlide(slide);
    }

    @Override
    @Transactional
    public SlideResponse createSlide(SlideDTO slideDTO) throws Exception {
        // Sanitize description
        String sanitizedDescription = HtmlSanitizer.sanitize(slideDTO.getDescription());
        // Find image entity based on publicId
        ImageEntity imageEntity = imageRepository.findByPublicId(slideDTO.getPublicId())
                .orElseThrow(() -> new RuntimeException("Image not found"));
        // Update image entity properties
        imageEntity.setIsUsed(true);
        imageEntity.setUsageCount(imageEntity.getUsageCount() + 1);
        // Save changes to the database
        imageRepository.save(imageEntity);
        // Nếu order được chỉ định và trùng với giá trị hiện có, điều chỉnh thứ tự
        Integer maxOrder = slideRepository.findMaxOrder();
        if (slideDTO.getOrder() == null || slideDTO.getOrder() == 0 || slideDTO.getOrder() > maxOrder + 1) {
            slideDTO.setOrder(maxOrder + 1);
        } else {
            slideRepository.incrementOrderFrom(slideDTO.getOrder());
        }

        // Create new slide
        SlideEntity newSlide = SlideEntity.builder()
                .title(slideDTO.getTitle())
                .description(sanitizedDescription)
                .imageUrl(slideDTO.getImageUrl())
                .publicId(slideDTO.getPublicId())
                .order( slideDTO.getOrder())
                .status(true)
                .link(slideDTO.getLink())
                .build();
        SlideEntity slideEntity = slideRepository.save(newSlide);

        return SlideResponse.fromSlide(slideEntity);
    }

    @Override
    public List<SlideResponse> getAllSlides() {
        return slideRepository.findAll().stream().map(SlideResponse::fromSlide).toList();
    }

    @Override
    public List<SlideResponse> findAllByStatusTrue() {
        return slideRepository.findAllByStatusTrue().stream().map(SlideResponse::fromSlide).toList();
    }

    @Override
    @Transactional
    public SlideResponse updateSlide(long slideId, SlideDTO slideDTO) throws Exception {
        // Tìm slide theo ID
        SlideEntity existingSlide = slideRepository.findById(slideId)
                .orElseThrow(() -> new RuntimeException("Slide not found"));
        // Cập nhật tiêu đề nếu không null hoặc rỗng
        if (slideDTO.getTitle() != null && !slideDTO.getTitle().isEmpty()) {
            if (!existingSlide.getTitle().equals(slideDTO.getTitle())) {
                boolean isTitleExists = slideRepository.existsByTitle(slideDTO.getTitle());
                if (isTitleExists) {
                    throw new IllegalArgumentException("Title already exists");
                }
            }
            existingSlide.setTitle(slideDTO.getTitle());
        }
        // Sanitize và cập nhật mô tả nếu không null hoặc rỗng
        if (StringUtils.hasText(slideDTO.getDescription())) {
            String sanitizedDescription = HtmlSanitizer.sanitize(slideDTO.getDescription());
            existingSlide.setDescription(sanitizedDescription);
        }
        // Cập nhật hình ảnh nếu có publicId mới
        if (StringUtils.hasText(slideDTO.getImageUrl()) && StringUtils.hasText(slideDTO.getPublicId())) {
            String newPublicId = slideDTO.getPublicId();
            // Tìm hình ảnh hiện tại dựa trên publicId
           if(!newPublicId.equals(existingSlide.getPublicId())){
               ImageEntity currentImage = imageRepository.findByPublicId(existingSlide.getPublicId())
                       .orElseThrow(() -> new RuntimeException("Current image not found"));
               // Giảm usageCount và cập nhật isUsed của hình ảnh hiện tại nếu cần
               currentImage.setUsageCount(currentImage.getUsageCount() - 1);
               if (currentImage.getUsageCount() <= 0) {
                   currentImage.setIsUsed(false);
               }
               imageRepository.save(currentImage);
               // Tìm hình ảnh mới dựa trên publicId
               ImageEntity newImage = imageRepository.findByPublicId(slideDTO.getPublicId())
                       .orElseThrow(() -> new RuntimeException("New image not found"));
               // Cập nhật thuộc tính isUsed và usageCount của hình ảnh mới
               newImage.setIsUsed(true);
               newImage.setUsageCount(newImage.getUsageCount() + 1);
               imageRepository.save(newImage);

               existingSlide.setImageUrl(slideDTO.getImageUrl());
               existingSlide.setPublicId(newPublicId);
           }
        }
        if(slideDTO.getStatus() != null){
            existingSlide.setStatus(slideDTO.getStatus());
        }
        // Kiểm tra và cập nhật link nếu không null hoặc rỗng
        if (StringUtils.hasText(slideDTO.getLink())) {
            existingSlide.setLink(slideDTO.getLink());
        }
        Integer oldOrder = existingSlide.getOrder();
        Integer newOrder = slideDTO.getOrder();
        Integer maxOrder = slideRepository.findMaxOrder();

        if (newOrder == null || newOrder == 0 || newOrder > maxOrder + 1) {
            newOrder = oldOrder;
        } else if (!oldOrder.equals(newOrder)) {
            if (newOrder > oldOrder) {
                slideRepository.decrementOrderBetween(oldOrder, newOrder);
            } else {
                slideRepository.incrementOrderBetween(newOrder, oldOrder);
            }
        }
        existingSlide.setOrder(newOrder);

        // Lưu slide đã cập nhật vào cơ sở dữ liệu
        SlideEntity updatedSlide = slideRepository.save(existingSlide);

        // Trả về đối tượng SlideResponse từ slide đã cập nhật
        return SlideResponse.fromSlide(updatedSlide);
    }

    @Override
    @Transactional
    public void deleteSlide(long id) throws Exception {
        SlideEntity existingSlide = slideRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find slide with id " + id));
        ImageEntity currentImage = imageRepository.findByPublicId(existingSlide.getPublicId())
                .orElseThrow(() -> new RuntimeException("Current image not found"));
        currentImage.setUsageCount(currentImage.getUsageCount() - 1);
        if (currentImage.getUsageCount() <= 0) {
            currentImage.setIsUsed(false);
        }
        imageRepository.save(currentImage);
        Integer order = existingSlide.getOrder();
        slideRepository.delete(existingSlide);
        slideRepository.decrementOrderAfterDelete(order);
    }

    @Override
    @Transactional
    public void updateSlideOrder(List<SlideOrderDTO> slideOrderDTOs) throws Exception {
        // Lấy danh sách các slide hiện tại từ cơ sở dữ liệu
        List<SlideEntity> currentSlides = slideRepository.findAll();

        // Tạo một map từ id của slide đến thứ tự mới
        Map<Long, Integer> newOrderMap = slideOrderDTOs.stream()
                .collect(Collectors.toMap(SlideOrderDTO::getId, SlideOrderDTO::getOrder));

        // Cập nhật thứ tự của các slide
        for (SlideEntity slide : currentSlides) {
            Integer newOrder = newOrderMap.get(slide.getId());
            if (newOrder != null) {
                Integer oldOrder = slide.getOrder();
                Integer maxOrder = slideRepository.findMaxOrder();

                if (newOrder == null || newOrder == 0 || newOrder > maxOrder + 1) {
                    newOrder = oldOrder;
                } else if (!oldOrder.equals(newOrder)) {
                    if (newOrder > oldOrder) {
                        slideRepository.decrementOrderBetween(oldOrder, newOrder);
                    } else {
                        slideRepository.incrementOrderBetween(newOrder, oldOrder);
                    }
                }
                slide.setOrder(newOrder);
            }
        }

        // Lưu các slide đã cập nhật vào cơ sở dữ liệu
        slideRepository.saveAll(currentSlides);
    }

}
