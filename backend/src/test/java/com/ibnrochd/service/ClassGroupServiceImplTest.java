package com.ibnrochd.service;

import com.ibnrochd.dto.request.ClassGroupRequest;
import com.ibnrochd.exception.ResourceNotFoundException;
import com.ibnrochd.model.ClassGroup;
import com.ibnrochd.model.ERole;
import com.ibnrochd.model.Role;
import com.ibnrochd.model.User;
import com.ibnrochd.repository.ClassGroupRepository;
import com.ibnrochd.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassGroupServiceImplTest {

    @Mock
    private ClassGroupRepository classGroupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClassGroupServiceImpl classGroupService;

    private ClassGroupRequest classGroupRequest;
    private ClassGroup classGroup;
    private User professeur;
    private User etudiant;

    @BeforeEach
    void setUp() {
        classGroupRequest = new ClassGroupRequest();
        classGroupRequest.setNomClasse("6ème A");
        classGroupRequest.setNiveauScolaire("6ème");
        classGroupRequest.setAnneeScolaire("2023-2024");
        classGroupRequest.setCapaciteMax(25);
        classGroupRequest.setIdProfesseurPrincipal("prof123");

        professeur = new User();
        professeur.setId("prof123");
        professeur.setRoles(Collections.singleton(new Role(ERole.ROLE_PROFESSEUR)));

        classGroup = new ClassGroup();
        classGroup.setId("class123");
        classGroup.setNomClasse("6ème A");
        classGroup.setNiveauScolaire("6ème");
        classGroup.setAnneeScolaire("2023-2024");
        classGroup.setCapaciteMax(25);
        classGroup.setIdProfesseurPrincipal("prof123");
        classGroup.setCreeLe(new Date());
        classGroup.setMisAJourLe(new Date());

        etudiant = new User();
        etudiant.setId("etudiant123");
        etudiant.setRoles(Collections.singleton(new Role(ERole.ROLE_ETUDIANT)));
        // Supposons que User a une sous-classe ou un champ pour les détails de l'étudiant
        // et que idClasse y est défini. Pour ce test, nous nous concentrons sur l'ID.
    }

    @Test
    void createClassGroup_shouldSaveAndReturnClassGroup() {
        when(userRepository.findById("prof123")).thenReturn(Optional.of(professeur));
        when(classGroupRepository.save(any(ClassGroup.class))).thenAnswer(invocation -> {
            ClassGroup cg = invocation.getArgument(0);
            cg.setId("newClassId"); // Simuler la sauvegarde et l'attribution d'un ID
            return cg;
        });

        ClassGroup created = classGroupService.createClassGroup(classGroupRequest);

        assertNotNull(created);
        assertEquals("6ème A", created.getNomClasse());
        assertEquals("prof123", created.getIdProfesseurPrincipal());
        assertNotNull(created.getCreeLe());
        assertNotNull(created.getMisAJourLe());
        verify(classGroupRepository, times(1)).save(any(ClassGroup.class));
    }

    @Test
    void createClassGroup_whenProfesseurNotFound_shouldThrowResourceNotFoundException() {
        when(userRepository.findById("prof123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            classGroupService.createClassGroup(classGroupRequest);
        });
        verify(classGroupRepository, never()).save(any(ClassGroup.class));
    }

    @Test
    void getClassGroupById_shouldReturnClassGroup_whenFound() {
        when(classGroupRepository.findById("class123")).thenReturn(Optional.of(classGroup));
        Optional<ClassGroup> found = classGroupService.getClassGroupById("class123");
        assertTrue(found.isPresent());
        assertEquals("6ème A", found.get().getNomClasse());
    }

    @Test
    void getClassGroupById_shouldReturnEmpty_whenNotFound() {
        when(classGroupRepository.findById("unknownId")).thenReturn(Optional.empty());
        Optional<ClassGroup> found = classGroupService.getClassGroupById("unknownId");
        assertFalse(found.isPresent());
    }

    @Test
    void updateClassGroup_shouldUpdateAndReturnClassGroup() {
        when(classGroupRepository.findById("class123")).thenReturn(Optional.of(classGroup));
        when(classGroupRepository.save(any(ClassGroup.class))).thenReturn(classGroup);

        ClassGroupRequest updateRequest = new ClassGroupRequest();
        updateRequest.setNomClasse("5ème B");
        updateRequest.setNiveauScolaire("5ème");
        updateRequest.setAnneeScolaire("2023-2024");
        updateRequest.setCapaciteMax(30);

        ClassGroup updated = classGroupService.updateClassGroup("class123", updateRequest);

        assertNotNull(updated);
        assertEquals("5ème B", updated.getNomClasse());
        assertEquals(30, updated.getCapaciteMax());
        verify(classGroupRepository, times(1)).save(classGroup);
    }

    @Test
    void deleteClassGroup_shouldCallDeleteById_whenFound() {
        when(classGroupRepository.existsById("class123")).thenReturn(true);
        doNothing().when(classGroupRepository).deleteById("class123");

        classGroupService.deleteClassGroup("class123");

        verify(classGroupRepository, times(1)).deleteById("class123");
    }

    @Test
    void deleteClassGroup_shouldThrowException_whenNotFound() {
        when(classGroupRepository.existsById("unknownId")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> classGroupService.deleteClassGroup("unknownId"));
        verify(classGroupRepository, never()).deleteById(anyString());
    }

    @Test
    void addStudentToClass_shouldAddStudentIdAndSave() {
        when(classGroupRepository.findById("class123")).thenReturn(Optional.of(classGroup));
        when(userRepository.findById("etudiant123")).thenReturn(Optional.of(etudiant));
        when(classGroupRepository.save(any(ClassGroup.class))).thenReturn(classGroup);

        ClassGroup updatedClassGroup = classGroupService.addStudentToClass("class123", "etudiant123");

        assertTrue(updatedClassGroup.getListeEtudiantsIds().contains("etudiant123"));
        verify(classGroupRepository, times(1)).save(classGroup);
        // Idéalement, vérifier aussi la mise à jour de l'étudiant si User.idClasse est géré ici
        // verify(userRepository, times(1)).save(etudiant);
    }

     @Test
    void addStudentToClass_whenClassNotFound_shouldThrowException() {
        when(classGroupRepository.findById("unknownClassId")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            classGroupService.addStudentToClass("unknownClassId", "etudiant123");
        });
    }

    @Test
    void addStudentToClass_whenStudentNotFound_shouldThrowException() {
        when(classGroupRepository.findById("class123")).thenReturn(Optional.of(classGroup));
        when(userRepository.findById("unknownStudentId")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            classGroupService.addStudentToClass("class123", "unknownStudentId");
        });
    }

    @Test
    void removeStudentFromClass_shouldRemoveStudentIdAndSave() {
        classGroup.getListeEtudiantsIds().add("etudiant123"); // Pré-remplir
        when(classGroupRepository.findById("class123")).thenReturn(Optional.of(classGroup));
        // Pas besoin de mock userRepository.findById pour la suppression simple de l'ID de la liste
        when(classGroupRepository.save(any(ClassGroup.class))).thenReturn(classGroup);

        ClassGroup updatedClassGroup = classGroupService.removeStudentFromClass("class123", "etudiant123");

        assertFalse(updatedClassGroup.getListeEtudiantsIds().contains("etudiant123"));
        verify(classGroupRepository, times(1)).save(classGroup);
    }
}