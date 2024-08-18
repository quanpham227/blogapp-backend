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

    public static ClientResponse fromClient (ClientEntity client){
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .description(client.getDescription())
                .logo(client.getLogo())
                .build();

    }
}
