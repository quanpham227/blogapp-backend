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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlideService implements ISlideService {
    private final SlideRepository slideRepository;
    private final ImageRepository imageRepository;

    @Override
    public boolean existsByTitle(String title) {
        return slideRepository.existsByTitle(title);
    }

    @Override
    public SlideResponse findById(long id) throws Exception {
        SlideEntity slide = slideRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find slide with id " + id));
        return SlideResponse.fromSlide(slide);
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
    public SlideResponse createSlide(SlideDTO slideDTO) throws Exception {
        // Sanitize description
        String sanitizedDescription = HtmlSanitizer.sanitize(slideDTO.getDescription());
        // Update image usage
        updateImageUsage(null, slideDTO.getPublicId());
        // Adjust order
        adjustSlideOrder(slideDTO);
        // Create new slide
        SlideEntity newSlide = SlideEntity.builder()
                .title(slideDTO.getTitle())
                .description(sanitizedDescription)
                .imageUrl(slideDTO.getImageUrl())
                .publicId(slideDTO.getPublicId())
                .order(slideDTO.getOrder())
                .status(true)
                .link(slideDTO.getLink())
                .build();
        SlideEntity slideEntity = slideRepository.save(newSlide);
        return SlideResponse.fromSlide(slideEntity);
    }

    @Override
    @Transactional
    public SlideResponse updateSlide(long slideId, SlideDTO slideDTO) throws Exception {
        // Find slide by ID
        SlideEntity existingSlide = slideRepository.findById(slideId)
                .orElseThrow(() -> new RuntimeException("Slide not found"));
        // Update title if not null or empty
        if (slideDTO.getTitle() != null && !slideDTO.getTitle().isEmpty()) {
            if (!existingSlide.getTitle().equals(slideDTO.getTitle())) {
                boolean isTitleExists = slideRepository.existsByTitle(slideDTO.getTitle());
                if (isTitleExists) {
                    throw new IllegalArgumentException("Title already exists");
                }
            }
            existingSlide.setTitle(slideDTO.getTitle());
        }
        // Sanitize and update description if not null or empty
        if (StringUtils.hasText(slideDTO.getDescription())) {
            String sanitizedDescription = HtmlSanitizer.sanitize(slideDTO.getDescription());
            existingSlide.setDescription(sanitizedDescription);
        }
        // Update image if new publicId is provided
        if (StringUtils.hasText(slideDTO.getImageUrl()) && StringUtils.hasText(slideDTO.getPublicId())) {
            updateImageUsage(existingSlide.getPublicId(), slideDTO.getPublicId());
            existingSlide.setImageUrl(slideDTO.getImageUrl());
            existingSlide.setPublicId(slideDTO.getPublicId());
        }
        // Update status if not null
        if (slideDTO.getStatus() != null) {
            existingSlide.setStatus(slideDTO.getStatus());
        }
        // Update link if not null or empty
        if (StringUtils.hasText(slideDTO.getLink())) {
            existingSlide.setLink(slideDTO.getLink());
        }
        // Adjust order
        adjustSlideOrder(existingSlide, slideDTO.getOrder());
        // Save updated slide
        SlideEntity updatedSlide = slideRepository.save(existingSlide);
        return SlideResponse.fromSlide(updatedSlide);
    }

    @Override
    @Transactional
    public void deleteSlide(long id) throws Exception {
        SlideEntity existingSlide = slideRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find slide with id " + id));
        updateImageUsage(existingSlide.getPublicId(), null);
        Integer order = existingSlide.getOrder();
        slideRepository.delete(existingSlide);
        slideRepository.decrementOrderAfterDelete(order);
    }

    @Override
    @Transactional
    public void updateSlideOrder(List<SlideOrderDTO> slideOrderDTOs) throws Exception {
        // Get current slides from the database
        List<SlideEntity> currentSlides = slideRepository.findAll();
        // Create a map from slide id to new order
        Map<Long, Integer> newOrderMap = slideOrderDTOs.stream()
                .collect(Collectors.toMap(SlideOrderDTO::getId, SlideOrderDTO::getOrder));
        // Update slide orders
        for (SlideEntity slide : currentSlides) {
            Integer newOrder = newOrderMap.get(slide.getId());
            if (newOrder != null) {
                adjustSlideOrder(slide, newOrder);
            }
        }
        // Save updated slides to the database
        slideRepository.saveAll(currentSlides);
    }

    private void updateImageUsage(String currentPublicId, String newPublicId) throws RuntimeException {
        if (currentPublicId != null) {
            ImageEntity currentImage = imageRepository.findByPublicId(currentPublicId)
                    .orElse(null);
            if (currentImage != null) {
                currentImage.setUsageCount(currentImage.getUsageCount() - 1);
                if (currentImage.getUsageCount() <= 0) {
                    currentImage.setIsUsed(false);
                }
                imageRepository.save(currentImage);
            } else {
                System.out.println("Warning: Current image not found, proceeding with new image.");
            }
        }
        if (newPublicId != null) {
            ImageEntity newImage = imageRepository.findByPublicId(newPublicId)
                    .orElseThrow(() -> new RuntimeException("New image not found"));
            newImage.setIsUsed(true);
            newImage.setUsageCount(newImage.getUsageCount() + 1);
            imageRepository.save(newImage);
        }
    }

    private void adjustSlideOrder(SlideDTO slideDTO) {
        Integer maxOrder = slideRepository.findMaxOrder();
        if (slideDTO.getOrder() == null || slideDTO.getOrder() == 0 || slideDTO.getOrder() > maxOrder + 1) {
            slideDTO.setOrder(maxOrder + 1);
        } else {
            slideRepository.incrementOrderFrom(slideDTO.getOrder());
        }
    }

    private void adjustSlideOrder(SlideEntity existingSlide, Integer newOrder) {
        Integer oldOrder = existingSlide.getOrder();
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
    }
}