package com.ibnrochd.service;

import com.ibnrochd.dto.request.ClassGroupRequest;
import com.ibnrochd.exception.ResourceNotFoundException;
import com.ibnrochd.model.ClassGroup;
import com.ibnrochd.model.User;
import com.ibnrochd.model.ERole;
import com.ibnrochd.repository.ClassGroupRepository;
import com.ibnrochd.repository.UserRepository;
import com.ibnrochd.repository.CourseRepository; // Décommenté
// import com.ibnrochd.model.Course; // À décommenter si vous avez une entité Course

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

    @Autowired // Décommenté
    private CourseRepository courseRepository; // Pour valider les IDs des cours

    @Override
    @Transactional
    public ClassGroup createClassGroup(ClassGroupRequest request) {
        ClassGroup classGroup = new ClassGroup();
        classGroup.setNomClasse(request.getNomClasse());
        classGroup.setNiveauScolaire(request.getNiveauScolaire());
        classGroup.setAnneeScolaire(request.getAnneeScolaire());
        classGroup.setCapaciteMax(request.getCapaciteMax());
        classGroup.setSallePrincipale(request.getSallePrincipale());

        if (request.getIdProfesseurPrincipal() != null && !request.getIdProfesseurPrincipal().isEmpty()) {
            // Valider que l'ID correspond à un professeur existant
            User professeur = userRepository.findById(request.getIdProfesseurPrincipal())
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_PROFESSEUR))
                .orElseThrow(() -> new ResourceNotFoundException("Professeur principal non trouvé ou invalide avec l'id: " + request.getIdProfesseurPrincipal()));
            classGroup.setIdProfesseurPrincipal(professeur.getId());
        }

        if (request.getListeCoursIds() != null && !request.getListeCoursIds().isEmpty()) {
            // Valider les IDs des cours s'ils sont fournis
            for (String courseId : request.getListeCoursIds()) {
                courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé avec l'id: " + courseId));
            }
            classGroup.setListeCoursIds(request.getListeCoursIds());
        } else {
            classGroup.setListeCoursIds(new java.util.HashSet<>()); // Initialiser si null
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

        if (request.getIdProfesseurPrincipal() != null && !request.getIdProfesseurPrincipal().isEmpty()) {
            User professeur = userRepository.findById(request.getIdProfesseurPrincipal())
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_PROFESSEUR))
                .orElseThrow(() -> new ResourceNotFoundException("Professeur principal non trouvé ou invalide pour la mise à jour avec l'id: " + request.getIdProfesseurPrincipal()));
            classGroup.setIdProfesseurPrincipal(professeur.getId());
        } else {
            classGroup.setIdProfesseurPrincipal(null); // Permettre de dé-assigner
        }

        if (request.getListeCoursIds() != null && !request.getListeCoursIds().isEmpty()) {
            // Valider les IDs des cours comme dans createClassGroup
            for (String courseId : request.getListeCoursIds()) {
                courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé pour la mise à jour avec l'id: " + courseId));
            }
            classGroup.setListeCoursIds(request.getListeCoursIds());
        } else {
            classGroup.setListeCoursIds(new java.util.HashSet<>()); // Initialiser si null ou vide
        }
        classGroup.setMisAJourLe(new Date());

        return classGroupRepository.save(classGroup);
    }

    @Override
    @Transactional // Important pour la cohérence des données lors des multiples sauvegardes potentielles
    public void deleteClassGroup(String id) {
        ClassGroup classGroup = getClassGroupById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée avec l'id: " + id));

        // Dé-assigner les étudiants de cette classe
        if (classGroup.getListeEtudiantsIds() != null) {
            for (String studentId : classGroup.getListeEtudiantsIds()) {
                userRepository.findById(studentId).ifPresent(student -> {
                    student.getDetailsEtudiant().setIdClasse(null);
                    student.setDateMiseAJour(new Date()); // Mettre à jour la date de modification de l'étudiant
                    userRepository.save(student);
                });
            }
        }
        classGroupRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClassGroup addStudentToClass(String classId, String studentId) {
        ClassGroup classGroup = getClassGroupById(classId).orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        User student = userRepository.findById(studentId)
            .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_ETUDIANT))
            .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé ou l'utilisateur n'est pas un étudiant avec l'id: " + studentId));

        if (classGroup.getListeEtudiantsIds().size() >= classGroup.getCapaciteMax()) {
            throw new IllegalStateException("La classe a atteint sa capacité maximale.");
        }

        if (student.getDetailsEtudiant().getIdClasse() != null && !student.getDetailsEtudiant().getIdClasse().equals(classId)) {
            throw new IllegalArgumentException("L'étudiant est déjà assigné à une autre classe: " + student.getDetailsEtudiant().getIdClasse());
        }
        if (classGroup.getListeEtudiantsIds().contains(studentId)) {
             // L'étudiant est déjà dans cette classe, rien à faire ou retourner la classe telle quelle.
            return classGroup;
        }

        classGroup.getListeEtudiantsIds().add(studentId);
        student.getDetailsEtudiant().setIdClasse(classId);
        student.setDateMiseAJour(new Date());
        userRepository.save(student);

        return classGroupRepository.save(classGroup);
    }

    @Override
    @Transactional
    public ClassGroup removeStudentFromClass(String classId, String studentId) {
        ClassGroup classGroup = getClassGroupById(classId).orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée"));
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'id: " + studentId));

        if (!classGroup.getListeEtudiantsIds().contains(studentId) || !classId.equals(student.getDetailsEtudiant().getIdClasse())) {
            throw new IllegalArgumentException("L'étudiant n'est pas assigné à cette classe ou l'assignation est incohérente.");
        }

        classGroup.getListeEtudiantsIds().remove(studentId);
        student.getDetailsEtudiant().setIdClasse(null);
        student.setDateMiseAJour(new Date());
        userRepository.save(student);
        return classGroupRepository.save(classGroup);
    }
}