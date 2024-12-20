package com.pivinadanang.blog.responses.client;

import com.pivinadanang.blog.models.ClientEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientResponse {
    private Long id;

    private String name;

    private String description;

    private String logo;

    private String publicId;


    public static ClientResponse fromClient (ClientEntity client){
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName() != null ? client.getName() : "") // Xử lý null với giá trị mặc định rỗng
                .description(client.getDescription() != null ? client.getDescription() : "") // Xử lý null với giá trị mặc định rỗng
                .logo(client.getLogo() != null ? client.getLogo() : "") // Xử lý null với giá trị mặc định rỗng
                .publicId(client.getPublicId() != null ? client.getPublicId() : "") // Xử lý null với giá trị mặc định rỗng
                .build();

    }
}
