package com.ibnrochd.repository;

import com.ibnrochd.model.ClassGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassGroupRepository extends MongoRepository<ClassGroup, String> {
    List<ClassGroup> findByAnneeScolaire(String anneeScolaire);
    List<ClassGroup> findByNiveauScolaireAndAnneeScolaire(String niveauScolaire, String anneeScolaire);
}