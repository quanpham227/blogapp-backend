package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.tag.TagResponse;
import com.pivinadanang.blog.services.tag.TagService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/top")
    public ResponseEntity<ResponseObject> getTopTags(@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int page) throws Exception {
        if (limit <= 0 || page < 0) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Invalid limit or page")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
        Pageable pageable = PageRequest.of(page, limit);
        List<TagResponse> tags = tagService.getTopTags(pageable);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get top tags successfully")
                .status(HttpStatus.OK)
                .data(tags)
                .build());
    }
}
