package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    boolean existsByName(String name);

}
