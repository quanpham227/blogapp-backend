package com.pivinadanang.blog.services.slide;

import com.pivinadanang.blog.dtos.SlideDTO;
import com.pivinadanang.blog.models.SlideEntity;
import com.pivinadanang.blog.responses.client.ClientResponse;
import com.pivinadanang.blog.responses.slide.SlideResponse;

import java.util.List;

public interface ISlideService {
    boolean existsByTitle(String title);
    SlideResponse findById(long id) throws Exception;
    SlideResponse createSlide(SlideDTO slideDTO) throws Exception;
    List<SlideResponse> getAllSlides();
    SlideResponse updateSlide(long slideId, SlideDTO slideDTO) throws Exception;
    void deleteSlide(long id) throws Exception;

}
