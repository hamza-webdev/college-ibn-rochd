// src/main/java/com/ibnrochd/dto/request/LoginRequest.java
package com.ibnrochd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour la requête de connexion.
 */
@Data
public class LoginRequest {
    @NotBlank(message = "L'email est requis")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    private String motDePasse;
}