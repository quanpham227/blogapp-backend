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
        try {
            SlideResponse slide = slideService.findById(slideId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(slide)
                    .message("Get slide information successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_NOT_FOUND, slideId))
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .build());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping
    public ResponseEntity<ResponseObject> insertSlide(@Valid @RequestBody SlideDTO slideDTO, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Validation errors")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(fieldErrors)
                    .build());
        }

        if (slideService.existsByTitle(slideDTO.getTitle())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_TITLE_ALREADY_EXISTS))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }

        SlideResponse slideResponse = slideService.createSlide(slideDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_SLIDE_SUCCESSFULLY))
                        .status(HttpStatus.OK)
                        .data(slideResponse)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateSlide(@Valid @RequestBody SlideDTO slideDTO, @PathVariable Long id, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Validation errors")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(result.getFieldErrors())
                    .build());
        }
        try {
            SlideResponse slideResponse = slideService.updateSlide(id, slideDTO);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_SLIDE_SUCCESSFULLY, id))
                    .status(HttpStatus.OK)
                    .data(slideResponse)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_NOT_FOUND, id))
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .build());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PutMapping("/order")
    public ResponseEntity<ResponseObject> updateSlideOrder(@Valid @RequestBody List<SlideOrderDTO> slideDTOs) throws Exception {
        try {
            slideService.updateSlideOrder(slideDTOs);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Update slides order successfully")
                    .status(HttpStatus.OK)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Invalid input")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteSlide(@PathVariable Long id) throws Exception {
        try {
            slideService.deleteSlide(id);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_SLIDE_SUCCESSFULLY, id))
                    .status(HttpStatus.OK)
                    .data(null)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.SLIDE_NOT_FOUND, id))
                    .status(HttpStatus.NOT_FOUND)
                    .data(null)
                    .build());
        }
    }
}
