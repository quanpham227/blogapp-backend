package com.pivinadanang.blog.controller;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.SlideDTO;
import com.pivinadanang.blog.dtos.SlideOrderDTO;
import com.pivinadanang.blog.models.SlideEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.slide.SlideResponse;
import com.pivinadanang.blog.services.slide.ISlideService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/slides")
@Validated
@RequiredArgsConstructor
public class SlideController {
    private final ISlideService slideService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("/admin")
    public ResponseEntity<ResponseObject> getAllSlides() {
        List<SlideResponse> slides = slideService.getAllSlides();
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_SLIDE_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(slides)
                .build());
    }
    @GetMapping("user")
    public ResponseEntity<ResponseObject> findAllByStatusTrue() {
        List<SlideResponse> slides = slideService.findAllByStatusTrue();
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_SLIDE_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(slides)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getSlideById(@PathVariable("id") Long slideId) throws Exception {
        SlideResponse slide = slideService.findById(slideId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(slide)
                .message("Get slide information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> insertSlide(@Valid @RequestBody SlideDTO slideDTO, BindingResult result) throws Exception {
        if (slideService.existsByTitle(slideDTO.getTitle())) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        SlideResponse slide = slideService.createSlide(slideDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_SLIDE_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(slide)
                .build());
    }

    @PutMapping(value = "{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateSlide(@Valid @RequestBody SlideDTO slideDTO,
                                                      @PathVariable Long id) throws Exception {
        SlideResponse slide = slideService.updateSlide(id, slideDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_SLIDE_SUCCESSFULLY, id))
                .status(HttpStatus.OK)
                .data(slide)
                .build());
    }

    @PutMapping("/order")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateSlideOrder(@Valid @RequestBody List<SlideOrderDTO> slideDTOs) throws Exception {
        slideService.updateSlideOrder(slideDTOs);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Update slides order successfully")
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteSlide(@PathVariable Long id) throws Exception {
        slideService.deleteSlide(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_SLIDE_SUCCESSFULLY, id))
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }
}
