package com.pivinadanang.blog.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clients")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false, length = 100)
    private String name;

    @Column (name="description",columnDefinition = "TEXT")
    private String description;

    @Column (name="logo", nullable = false, length = 100)
    private String logo;

    @Column (name="file_id", nullable = false)
    private String fileId;

}
