package com.ibnrochd.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * Modèle représentant un étudiant.
 * Un étudiant est lié à un utilisateur (User) ayant le rôle ETUDIANT.
 */
@Document(collection = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    private String id;

    @NotBlank(message = "Le matricule ne peut pas être vide")
    @Indexed(unique = true)
    private String matricule;

    @NotBlank(message = "Le niveau ne peut pas être vide")
    private String niveau;

    private Date dateInscription = new Date();

    private String userId; // Référence à l'utilisateur associé (User)
}
