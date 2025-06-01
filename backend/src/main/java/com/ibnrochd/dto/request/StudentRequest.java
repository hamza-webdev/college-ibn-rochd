package com.ibnrochd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour la création ou la mise à jour d'un étudiant.
 */
@Data
public class StudentRequest {
    @NotBlank(message = "Le matricule est requis")
    private String matricule;

    @NotBlank(message = "Le niveau est requis")
    private String niveau;

    @NotBlank(message = "L'identifiant utilisateur est requis")
    private String userId;
}
