package com.ibnrochd.repository;

import com.ibnrochd.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    Optional<Course> findByNomCours(String nomCours);
    Optional<Course> findByCodeCours(String codeCours);
}