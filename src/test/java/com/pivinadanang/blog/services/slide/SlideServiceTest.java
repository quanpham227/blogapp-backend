package com.pivinadanang.blog.services.slide;


import com.pivinadanang.blog.dtos.SlideDTO;
import com.pivinadanang.blog.dtos.SlideOrderDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.models.SlideEntity;
import com.pivinadanang.blog.repositories.ImageRepository;
import com.pivinadanang.blog.repositories.SlideRepository;
import com.pivinadanang.blog.responses.slide.SlideResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlideServiceTest {

    @Mock
    private SlideRepository slideRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private SlideService slideService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExistsByTitle() {
        when(slideRepository.existsByTitle("Test Title")).thenReturn(true);
        boolean exists = slideService.existsByTitle("Test Title");
        assertTrue(exists);
    }

    @Test
    void testFindById_Success() throws Exception {
        SlideEntity slide = new SlideEntity();
        slide.setId(1L);
        when(slideRepository.findById(1L)).thenReturn(Optional.of(slide));
        SlideResponse response = slideService.findById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void testFindById_NotFound() {
        when(slideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> slideService.findById(1L));
    }

    @Test
    void testGetAllSlides() {
        SlideEntity slide1 = new SlideEntity();
        slide1.setId(1L);
        SlideEntity slide2 = new SlideEntity();
        slide2.setId(2L);
        when(slideRepository.findAll()).thenReturn(Arrays.asList(slide1, slide2));
        List<SlideResponse> slides = slideService.getAllSlides();
        assertNotNull(slides);
        assertEquals(2, slides.size());
    }

    @Test
    void testFindAllByStatusTrue() {
        SlideEntity slide1 = new SlideEntity();
        slide1.setId(1L);
        slide1.setStatus(true);
        when(slideRepository.findAllByStatusTrue()).thenReturn(Arrays.asList(slide1));
        List<SlideResponse> slides = slideService.findAllByStatusTrue();
        assertNotNull(slides);
        assertEquals(1, slides.size());
    }

    @Test
    void testCreateSlide() throws Exception {
        SlideDTO slideDTO = new SlideDTO();
        slideDTO.setTitle("Test Title");
        slideDTO.setDescription("Test Description");
        slideDTO.setImageUrl("http://example.com/image.jpg");
        slideDTO.setPublicId("publicId");
        slideDTO.setOrder(1);

        SlideEntity slideEntity = new SlideEntity();
        slideEntity.setId(1L);

        when(slideRepository.save(any(SlideEntity.class))).thenReturn(slideEntity);
        when(imageRepository.findByPublicId("publicId")).thenReturn(Optional.of(new ImageEntity()));

        SlideResponse response = slideService.createSlide(slideDTO);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void testUpdateSlide_Success() throws Exception {
        SlideDTO slideDTO = new SlideDTO();
        slideDTO.setTitle("Updated Title");
        slideDTO.setDescription("Updated Description");
        slideDTO.setImageUrl("http://example.com/updated_image.jpg");
        slideDTO.setPublicId("updatedPublicId");
        slideDTO.setOrder(2);

        SlideEntity existingSlide = new SlideEntity();
        existingSlide.setId(1L);
        existingSlide.setTitle("Old Title");
        existingSlide.setPublicId("oldPublicId");

        when(slideRepository.findById(1L)).thenReturn(Optional.of(existingSlide));
        when(slideRepository.save(any(SlideEntity.class))).thenReturn(existingSlide);
        when(imageRepository.findByPublicId("updatedPublicId")).thenReturn(Optional.of(new ImageEntity()));

        SlideResponse response = slideService.updateSlide(1L, slideDTO);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Updated Title", response.getTitle());
    }

    @Test
    void testUpdateSlide_NotFound() {
        SlideDTO slideDTO = new SlideDTO();
        when(slideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> slideService.updateSlide(1L, slideDTO));
    }

    @Test
    void testDeleteSlide_Success() throws Exception {
        SlideEntity existingSlide = new SlideEntity();
        existingSlide.setId(1L);
        existingSlide.setPublicId("publicId");
        existingSlide.setOrder(1);

        when(slideRepository.findById(1L)).thenReturn(Optional.of(existingSlide));
        doNothing().when(slideRepository).delete(existingSlide);

        slideService.deleteSlide(1L);
        verify(slideRepository, times(1)).delete(existingSlide);
    }

    @Test
    void testDeleteSlide_NotFound() {
        when(slideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> slideService.deleteSlide(1L));
    }

    @Test
    void testUpdateSlideOrder() throws Exception {
        SlideOrderDTO slideOrderDTO1 = new SlideOrderDTO(1L, 2);
        SlideOrderDTO slideOrderDTO2 = new SlideOrderDTO(2L, 1);

        SlideEntity slide1 = new SlideEntity();
        slide1.setId(1L);
        slide1.setOrder(1);

        SlideEntity slide2 = new SlideEntity();
        slide2.setId(2L);
        slide2.setOrder(2);

        when(slideRepository.findAll()).thenReturn(Arrays.asList(slide1, slide2));

        slideService.updateSlideOrder(Arrays.asList(slideOrderDTO1, slideOrderDTO2));

        verify(slideRepository, times(1)).saveAll(anyList());
    }
}