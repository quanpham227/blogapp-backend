package com.pivinadanang.blog.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "title", nullable = false, length = 100)
    private String title;

    @Column (name = "value", nullable = false)
    private Integer value;

    @Column (name = "description", length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

}
