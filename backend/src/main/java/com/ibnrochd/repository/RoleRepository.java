// src/main/java/com/ibnrochd/repository/RoleRepository.java
package com.ibnrochd.repository;

import com.ibnrochd.model.Role;
import com.ibnrochd.model.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data MongoDB pour l'entité Role.
 */
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}