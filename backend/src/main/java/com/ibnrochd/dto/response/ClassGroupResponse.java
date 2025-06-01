package com.ibnrochd.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassGroupResponse {
    private String id;
    private String nomClasse;
    private String niveauScolaire;
    private String anneeScolaire;
    private String idProfesseurPrincipal;
    private UserInfoResponse professeurPrincipal; // Pourrait être enrichi
    private Set<String> listeEtudiantsIds;
    // private List<UserInfoResponse> etudiants; // Pourrait être enrichi
    private Integer capaciteMax;
    private Set<String> listeCoursIds;
    // private List<CourseResponse> cours; // Pourrait être enrichi
    private String sallePrincipale;
    private Date creeLe;
    private Date misAJourLe;
}