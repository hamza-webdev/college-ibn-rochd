package com.ibnrochd.service;

import com.ibnrochd.dto.request.ClassGroupRequest;
import com.ibnrochd.exception.ResourceNotFoundException;
import com.ibnrochd.model.ClassGroup;
import com.ibnrochd.model.User;
import com.ibnrochd.model.ERole;
import com.ibnrochd.repository.ClassGroupRepository;
import com.ibnrochd.repository.UserRepository; // Assurez-vous que UserRepository est injecté

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClassGroupServiceImpl implements ClassGroupService {

    @Autowired
    private ClassGroupRepository classGroupRepository;

    @Autowired
    private UserRepository userRepository; // Pour valider/mettre à jour les étudiants/professeurs

    // @Autowired
    // private CourseRepository courseRepository; // Si vous gérez les cours

    @Override
    public ClassGroup createClassGroup(ClassGroupRequest request) {
        ClassGroup classGroup = new ClassGroup();
        classGroup.setNomClasse(request.getNomClasse());
        classGroup.setNiveauScolaire(request.getNiveauScolaire());
        classGroup.setAnneeScolaire(request.getAnneeScolaire());
        classGroup.setCapaciteMax(request.getCapaciteMax());
        classGroup.setSallePrincipale(request.getSallePrincipale());

        if (request.getIdProfesseurPrincipal() != null) {
            // Optionnel: Valider que l'ID correspond à un professeur existant
            userRepository.findById(request.getIdProfesseurPrincipal())
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_PROFESSEUR))
                .orElseThrow(() -> new ResourceNotFoundException("Professeur principal non trouvé ou invalide avec l'id: " + request.getIdProfesseurPrincipal()));
            classGroup.setIdProfesseurPrincipal(request.getIdProfesseurPrincipal());
        }

        if (request.getListeCoursIds() != null) {
            // Optionnel: Valider les IDs des cours
            classGroup.setListeCoursIds(request.getListeCoursIds());
        }

        classGroup.setCreeLe(new Date());
        classGroup.setMisAJourLe(new Date());
        return classGroupRepository.save(classGroup);
    }

    @Override
    public Optional<ClassGroup> getClassGroupById(String id) {
        return classGroupRepository.findById(id);
    }

    @Override
    public List<ClassGroup> getAllClassGroups() {
        return classGroupRepository.findAll();
    }

    @Override
    public List<ClassGroup> getClassGroupsByAnneeScolaire(String anneeScolaire) {
        return classGroupRepository.findByAnneeScolaire(anneeScolaire);
    }

    @Override
    @Transactional
    public ClassGroup updateClassGroup(String id, ClassGroupRequest request) {
        ClassGroup classGroup = getClassGroupById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée avec l'id: " + id));

        classGroup.setNomClasse(request.getNomClasse());
        classGroup.setNiveauScolaire(request.getNiveauScolaire());
        classGroup.setAnneeScolaire(request.getAnneeScolaire());
        classGroup.setCapaciteMax(request.getCapaciteMax());
        classGroup.setSallePrincipale(request.getSallePrincipale());
        classGroup.setIdProfesseurPrincipal(request.getIdProfesseurPrincipal()); // Ajouter validation si nécessaire
        classGroup.setListeCoursIds(request.getListeCoursIds()); // Ajouter validation si nécessaire
        classGroup.setMisAJourLe(new Date());

        return classGroupRepository.save(classGroup);
    }

    @Override
    public void deleteClassGroup(String id) {
        if (!classGroupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classe non trouvée avec l'id: " + id);
        }
        // Logique additionnelle: dé-assigner les étudiants de cette classe, etc.
        classGroupRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClassGroup addStudentToClass(String classId, String studentId) {
        ClassGroup classGroup = getClassGroupById(classId).orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        User student = userRepository.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));
        // Valider que l'utilisateur est un étudiant, que la classe n'est pas pleine, etc.
        classGroup.getListeEtudiantsIds().add(studentId);
        // Mettre à jour User.detailsEtudiant.idClasse (nécessite que User.java ait cette structure)
        // student.getDetailsEtudiant().setIdClasse(classId); userRepository.save(student);
        return classGroupRepository.save(classGroup);
    }

    @Override
    @Transactional
    public ClassGroup removeStudentFromClass(String classId, String studentId) {
        ClassGroup classGroup = getClassGroupById(classId).orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        // Valider que l'étudiant est dans la classe
        classGroup.getListeEtudiantsIds().remove(studentId);
        // Mettre à jour User.detailsEtudiant.idClasse à null
        // User student = userRepository.findById(studentId).orElse(null);
        // if(student != null) { student.getDetailsEtudiant().setIdClasse(null); userRepository.save(student); }
        return classGroupRepository.save(classGroup);
    }
}