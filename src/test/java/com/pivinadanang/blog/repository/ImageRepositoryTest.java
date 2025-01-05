package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.ImageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        ImageEntity image1 = ImageEntity.builder()
                .imageUrl("http://example.com/image1.jpg")
                .publicId("publicId1")
                .fileName("image1.jpg")
                .objectType("post")
                .fileType("jpg")
                .fileSize(1024L)
                .isUsed(false)
                .usageCount(0)
                .build();

        ImageEntity image2 = ImageEntity.builder()
                .imageUrl("http://example.com/image2.jpg")
                .publicId("publicId2")
                .fileName("image2.jpg")
                .objectType("post")
                .fileType("jpg")
                .fileSize(2048L)
                .isUsed(true)
                .usageCount(1)
                .build();

        imageRepository.save(image1);
        imageRepository.save(image2);
    }

    @Test
    void testSearchImages() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImageEntity> result = imageRepository.searchImages("image", "post", pageable);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void testFindUnusedImages() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImageEntity> result = imageRepository.findUnusedImages(pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPublicId()).isEqualTo("publicId1");
    }



    @Test
    void testFindByPublicId() {
        Optional<ImageEntity> result = imageRepository.findByPublicId("publicId1");
        assertThat(result).isPresent();
        assertThat(result.get().getImageUrl()).isEqualTo("http://example.com/image1.jpg");
    }

    @Test
    void testFindByImageUrl() {
        Optional<ImageEntity> result = imageRepository.findByImageUrl("http://example.com/image1.jpg");
        assertThat(result).isPresent();
        assertThat(result.get().getPublicId()).isEqualTo("publicId1");
    }
}