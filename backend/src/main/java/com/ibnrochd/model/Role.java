// src/main/java/com/ibnrochd/model/Role.java
package com.ibnrochd.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed; // Ajout pour l'unicité du nom

/**
 * Modèle représentant un rôle utilisateur (ex: ROLE_ETUDIANT, ROLE_PROFESSEUR, ROLE_ADMIN).
 * Le nom de la collection MongoDB sera "roles".
 */
@Document(collection = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    private String id;

    @Indexed(unique = true) // Assure que chaque nom de rôle est unique
    private ERole name;

    public Role(ERole name) {
        this.name = name;
    }
}