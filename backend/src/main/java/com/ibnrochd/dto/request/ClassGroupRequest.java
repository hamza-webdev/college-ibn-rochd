package com.ibnrochd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class ClassGroupRequest {

    @NotBlank(message = "Le nom de la classe ne peut pas être vide.")
    @Size(min = 2, max = 50, message = "Le nom de la classe doit contenir entre 2 et 50 caractères.")
    private String nomClasse;

    @NotBlank(message = "Le niveau scolaire ne peut pas être vide.")
    private String niveauScolaire;

    @NotBlank(message = "L'année scolaire ne peut pas être vide.")
    private String anneeScolaire;

    private String idProfesseurPrincipal; // Optionnel à la création, peut être assigné plus tard

    @NotNull(message = "La capacité maximale ne peut pas être nulle.")
    private Integer capaciteMax;

    private Set<String> listeCoursIds; // Optionnel à la création

    private String sallePrincipale;
}