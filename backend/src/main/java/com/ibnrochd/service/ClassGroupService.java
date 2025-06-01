package com.ibnrochd.service;

import com.ibnrochd.dto.request.ClassGroupRequest;
import com.ibnrochd.model.ClassGroup;

import java.util.List;
import java.util.Optional;

public interface ClassGroupService {
    ClassGroup createClassGroup(ClassGroupRequest classGroupRequest);
    Optional<ClassGroup> getClassGroupById(String id);
    List<ClassGroup> getAllClassGroups();
    List<ClassGroup> getClassGroupsByAnneeScolaire(String anneeScolaire);
    ClassGroup updateClassGroup(String id, ClassGroupRequest classGroupRequest);
    void deleteClassGroup(String id);

    ClassGroup addStudentToClass(String classId, String studentId);
    ClassGroup removeStudentFromClass(String classId, String studentId);
    // Méthodes similaires pour les cours et le professeur principal
}