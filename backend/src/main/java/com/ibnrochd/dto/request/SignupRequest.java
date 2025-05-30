// src/main/java/com/ibnrochd/dto/request/SignupRequest.java
package com.ibnrochd.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

/**
 * DTO pour la requête d'inscription.
 */
@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String prenom;

    @NotBlank
    @Size(min = 2, max = 50)
    private String nom;

    @NotBlank
    @Size(max = 70)
    @Email
    private String email;

    private Set<String> role; // Noms des rôles sous forme de String (ex: "admin", "etudiant")

    @NotBlank
    @Size(min = 8, max = 120)
    private String motDePasse;
}