package com.ibnrochd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibnrochd.dto.request.ClassGroupRequest;
import com.ibnrochd.dto.response.ClassGroupResponse;
import com.ibnrochd.model.ClassGroup;
import com.ibnrochd.service.ClassGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

// Importer AppConfig si ModelMapper y est défini, sinon le définir ici ou dans une classe de config de test
import com.ibnrochd.config.AppConfig; // Assurez-vous que ce chemin est correct

@WebMvcTest(ClassController.class)
@Import(AppConfig.class) // Pour que ModelMapper soit disponible
public class ClassControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassGroupService classGroupService;

    @Autowired
    private ObjectMapper objectMapper; // Fourni par Spring Boot pour la sérialisation JSON

    @Autowired
    private ModelMapper modelMapper; // Injecté grâce à @Import(AppConfig.class)

    private ClassGroup classGroup1;
    private ClassGroup classGroup2;
    private ClassGroupRequest classGroupRequest;
    private ClassGroupResponse classGroupResponse1;

    @BeforeEach
    void setUp() {
        classGroupRequest = new ClassGroupRequest();
        classGroupRequest.setNomClasse("Terminale S1");
        classGroupRequest.setNiveauScolaire("Terminale");
        classGroupRequest.setAnneeScolaire("2024-2025");
        classGroupRequest.setCapaciteMax(30);

        classGroup1 = new ClassGroup("classId1", "Terminale S1", "Terminale", "2024-2025", "prof1", null, 30, null, "Salle 101", new Date(), new Date());
        classGroup2 = new ClassGroup("classId2", "Première L", "Première", "2024-2025", "prof2", null, 28, null, "Salle 102", new Date(), new Date());

        // Utiliser ModelMapper pour convertir l'entité en réponse pour les mocks
        classGroupResponse1 = modelMapper.map(classGroup1, ClassGroupResponse.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simule un utilisateur avec le rôle ADMIN
    void createClassGroup_shouldReturnCreated() throws Exception {
        given(classGroupService.createClassGroup(any(ClassGroupRequest.class))).willReturn(classGroup1);

        mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classGroupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomClasse", is(classGroup1.getNomClasse())));
    }

    @Test
    @WithMockUser(roles = "PROFESSEUR") // Ou ETUDIANT, ADMIN
    void getClassGroupById_shouldReturnClassGroup() throws Exception {
        given(classGroupService.getClassGroupById("classId1")).willReturn(Optional.of(classGroup1));

        mockMvc.perform(get("/api/classes/classId1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("classId1")))
                .andExpect(jsonPath("$.nomClasse", is(classGroup1.getNomClasse())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllClassGroups_shouldReturnListOfClassGroups() throws Exception {
        given(classGroupService.getAllClassGroups()).willReturn(Arrays.asList(classGroup1, classGroup2));

        mockMvc.perform(get("/api/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nomClasse", is(classGroup1.getNomClasse())))
                .andExpect(jsonPath("$[1].nomClasse", is(classGroup2.getNomClasse())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateClassGroup_shouldReturnUpdatedClassGroup() throws Exception {
        ClassGroupRequest updateRequest = new ClassGroupRequest();
        updateRequest.setNomClasse("Terminale S1 Modifiée");
        updateRequest.setNiveauScolaire("Terminale");
        updateRequest.setAnneeScolaire("2024-2025");
        updateRequest.setCapaciteMax(32);

        ClassGroup updatedClassGroup = new ClassGroup("classId1", "Terminale S1 Modifiée", "Terminale", "2024-2025", "prof1", null, 32, null, "Salle 101", new Date(), new Date());

        given(classGroupService.updateClassGroup(anyString(), any(ClassGroupRequest.class))).willReturn(updatedClassGroup);

        mockMvc.perform(put("/api/classes/classId1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomClasse", is("Terminale S1 Modifiée")))
                .andExpect(jsonPath("$.capaciteMax", is(32)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteClassGroup_shouldReturnOkMessage() throws Exception {
        doNothing().when(classGroupService).deleteClassGroup("classId1");

        mockMvc.perform(delete("/api/classes/classId1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Classe supprimée avec succès!")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStudentToClass_shouldReturnUpdatedClassGroup() throws Exception {
        // Supposons que l'ajout d'un étudiant modifie la liste des IDs étudiants
        classGroup1.getListeEtudiantsIds().add("student123"); // Simuler l'ajout
        given(classGroupService.addStudentToClass("classId1", "student123")).willReturn(classGroup1);

        mockMvc.perform(post("/api/classes/classId1/students/student123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("classId1")))
                .andExpect(jsonPath("$.listeEtudiantsIds", org.hamcrest.Matchers.hasItem("student123")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeStudentFromClass_shouldReturnUpdatedClassGroup() throws Exception {
        classGroup1.getListeEtudiantsIds().add("student123"); // Pré-remplir
        ClassGroup classGroupAfterRemove = modelMapper.map(classGroup1, ClassGroup.class); // Cloner
        classGroupAfterRemove.getListeEtudiantsIds().remove("student123"); // Simuler la suppression

        given(classGroupService.removeStudentFromClass("classId1", "student123")).willReturn(classGroupAfterRemove);

        mockMvc.perform(delete("/api/classes/classId1/students/student123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("classId1")))
                .andExpect(jsonPath("$.listeEtudiantsIds", org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem("student123"))));
    }

    // Ajouter des tests pour les cas non autorisés (sans @WithMockUser ou avec un rôle insuffisant)
    @Test
    void createClassGroup_withoutAuth_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classGroupRequest)))
                .andExpect(status().isUnauthorized()); // Ou 403 Forbidden si Spring Security est configuré ainsi
    }
}