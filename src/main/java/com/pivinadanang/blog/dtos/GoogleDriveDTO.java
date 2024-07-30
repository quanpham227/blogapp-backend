package com.pivinadanang.blog.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleDriveDTO {
    private String url;
    private String fileId;
}
