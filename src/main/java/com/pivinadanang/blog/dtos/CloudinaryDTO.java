package com.pivinadanang.blog.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloudinaryDTO {
    private String url;
    private String publicId;
}
