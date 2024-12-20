package com.pivinadanang.blog.services.about;


import com.pivinadanang.blog.dtos.AboutDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.AboutEntity;
import com.pivinadanang.blog.repositories.AboutRepository;
import com.pivinadanang.blog.responses.about.AboutResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AboutServiceTest {

    @Mock
    private AboutRepository aboutRepository;

    @InjectMocks
    private AboutService aboutService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAbout_Success() throws Exception {
        // Mock AboutEntity
        AboutEntity aboutEntity = AboutEntity.builder()
                .uniqueKey("about_page")
                .title("About Us")
                .content("This is the about page content.")
                .imageUrl("http://example.com/image.jpg")
                .address("123 Main St")
                .phoneNumber("123-456-7890")
                .email("info@example.com")
                .workingHours("7 AM - 04:30 PM")
                .facebookLink("http://facebook.com/pivinadanang")
                .youtube("http://youtube.com/pivinadanang")
                .visionStatement("Our vision statement.")
                .foundingDate("2016-01-01")
                .ceoName("John Doe")
                .build();

        when(aboutRepository.findByUniqueKey("about_page")).thenReturn(Optional.of(aboutEntity));

        // Call the service method
        AboutResponse aboutResponse = aboutService.getAbout();

        // Assertions
        assertEquals("About Us", aboutResponse.getTitle());
        assertEquals("This is the about page content.", aboutResponse.getContent());
        assertEquals("http://example.com/image.jpg", aboutResponse.getImageUrl());
        assertEquals("123 Main St", aboutResponse.getAddress());
        assertEquals("123-456-7890", aboutResponse.getPhoneNumber());
        assertEquals("info@example.com", aboutResponse.getEmail());
        assertEquals("7 AM - 04:30 PM", aboutResponse.getWorkingHours());
        assertEquals("http://facebook.com/pivinadanang", aboutResponse.getFacebookLink());
        assertEquals("http://youtube.com/pivinadanang", aboutResponse.getYoutube());
        assertEquals("Our vision statement.", aboutResponse.getVisionStatement());
        assertEquals("2016-01-01", aboutResponse.getFoundingDate());
        assertEquals("John Doe", aboutResponse.getCeoName());
    }

    @Test
    public void testGetAbout_NotFound() {
        when(aboutRepository.findByUniqueKey("about_page")).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            aboutService.getAbout();
        });

        // Assertions
        assertEquals("Cannot find about page", exception.getMessage());
    }

    @Test
    public void testUpdateAbout_Success() throws Exception {
        // Mock AboutDTO
        AboutDTO aboutDTO = AboutDTO.builder()
                .title("Updated About Us")
                .content("Updated content.")
                .imageUrl("http://example.com/updated-image.jpg")
                .address("456 Main St")
                .phoneNumber("987-654-3210")
                .email("updated@example.com")
                .workingHours("8 AM - 05:30 PM")
                .facebookLink("http://facebook.com/updated")
                .youtube("http://youtube.com/updated")
                .visionStatement("Updated vision statement.")
                .foundingDate("2017-01-01")
                .ceoName("Jane Doe")
                .build();

        // Mock AboutEntity
        AboutEntity aboutEntity = AboutEntity.builder()
                .id(1L)
                .uniqueKey("about_page")
                .title("About Us")
                .content("This is the about page content.")
                .imageUrl("http://example.com/image.jpg")
                .address("123 Main St")
                .phoneNumber("123-456-7890")
                .email("info@example.com")
                .workingHours("7 AM - 04:30 PM")
                .facebookLink("http://facebook.com/pivinadanang")
                .youtube("http://youtube.com/pivinadanang")
                .visionStatement("Our vision statement.")
                .foundingDate("2016-01-01")
                .ceoName("John Doe")
                .build();

        when(aboutRepository.findById(1L)).thenReturn(Optional.of(aboutEntity));
        when(aboutRepository.save(any(AboutEntity.class))).thenReturn(aboutEntity);

        // Call the service method
        AboutResponse aboutResponse = aboutService.updateAbout(1L, aboutDTO);

        // Assertions
        assertEquals("Updated About Us", aboutResponse.getTitle());
        assertEquals("Updated content.", aboutResponse.getContent());
        assertEquals("http://example.com/updated-image.jpg", aboutResponse.getImageUrl());
        assertEquals("456 Main St", aboutResponse.getAddress());
        assertEquals("987-654-3210", aboutResponse.getPhoneNumber());
        assertEquals("updated@example.com", aboutResponse.getEmail());
        assertEquals("8 AM - 05:30 PM", aboutResponse.getWorkingHours());
        assertEquals("http://facebook.com/updated", aboutResponse.getFacebookLink());
        assertEquals("http://youtube.com/updated", aboutResponse.getYoutube());
        assertEquals("Updated vision statement.", aboutResponse.getVisionStatement());
        assertEquals("2017-01-01", aboutResponse.getFoundingDate());
        assertEquals("Jane Doe", aboutResponse.getCeoName());
    }

    @Test
    public void testUpdateAbout_NotFound() {
        // Mock AboutDTO
        AboutDTO aboutDTO = AboutDTO.builder()
                .title("Updated About Us")
                .content("Updated content.")
                .build();

        when(aboutRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(DataNotFoundException.class, () -> {
            aboutService.updateAbout(1L, aboutDTO);
        });

        // Assertions
        assertEquals("Cannot find about with id 1", exception.getMessage());
    }
}