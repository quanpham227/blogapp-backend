package com.pivinadanang.blog.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "categories")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false, length = 100)
    private String name;

    @Column (name="code", nullable = false, length = 100)
    private String code;

    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private Set<PostEntity> posts = new HashSet<>();

}
