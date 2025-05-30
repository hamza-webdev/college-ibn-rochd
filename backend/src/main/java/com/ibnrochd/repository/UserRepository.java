// src/main/java/com/ibnrochd/repository/UserRepository.java
package com.ibnrochd.repository;

import com.ibnrochd.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository Spring Data MongoDB pour l'entité User.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}