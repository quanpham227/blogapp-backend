package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    boolean existsByName(String name);

}
