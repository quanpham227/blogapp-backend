package com.pivinadanang.blog.dtos;

import lombok.*;

@Data//toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostImageDTO {

    private String imageUrl;

    private String fileId;
}
