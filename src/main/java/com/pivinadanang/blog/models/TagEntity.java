package com.pivinadanang.blog.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import jakarta.persistence.*;


import java.util.*;

@Entity
@Table(name = "tags")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class TagEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 100)
    private String slug;

    @ManyToMany(mappedBy = "tags")
    private Set<PostEntity> posts = new HashSet<>();

    // Constructor nhận tham số name
    public TagEntity(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
