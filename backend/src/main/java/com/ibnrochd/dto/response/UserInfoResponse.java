// src/main/java/com/ibnrochd/dto/response/UserInfoResponse.java
package com.ibnrochd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * DTO pour la réponse d'information utilisateur après connexion/inscription.
 */
@Data
@AllArgsConstructor
public class UserInfoResponse {
    private String id;
    private String prenom;
    private String nom;
    private String email;
    private List<String> roles;
    private String token; // Le token JWT
    private String type = "Bearer";

    public UserInfoResponse(String id, String prenom, String nom, String email, List<String> roles, String token) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.roles = roles;
        this.token = token;
    }
}