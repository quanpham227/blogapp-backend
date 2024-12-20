package com.pivinadanang.blog.services.tag;

import com.pivinadanang.blog.models.TagEntity;
import com.pivinadanang.blog.repositories.TagRepository;
import com.pivinadanang.blog.responses.tag.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTopTags() {
        TagEntity tag1 = new TagEntity();
        tag1.setId(1L);
        tag1.setName("Tag1");

        TagEntity tag2 = new TagEntity();
        tag2.setId(2L);
        tag2.setName("Tag2");

        Pageable pageable = PageRequest.of(0, 10);
        when(tagRepository.findTopTags(pageable)).thenReturn(Arrays.asList(tag1, tag2));

        List<TagResponse> topTags = tagService.getTopTags(pageable);

        assertNotNull(topTags);
        assertEquals(2, topTags.size());
        assertEquals("Tag1", topTags.get(0).getName());
        assertEquals("Tag2", topTags.get(1).getName());
    }
}