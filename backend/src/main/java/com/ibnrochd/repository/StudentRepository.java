package com.ibnrochd.repository;

import com.ibnrochd.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository Spring Data MongoDB pour l'entité Student.
 */
@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByMatricule(String matricule);
    List<Student> findByNiveau(String niveau);
    List<Student> findByUserId(String userId);
}
