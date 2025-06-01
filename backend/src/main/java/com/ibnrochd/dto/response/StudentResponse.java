package com.ibnrochd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

/**
 * DTO pour la réponse d'un étudiant.
 */
@Data
@AllArgsConstructor
public class StudentResponse {
    private String id;
    private String matricule;
    private String niveau;
    private Date dateInscription;
    private String userId;
}
