// src/main/java/com/ibnrochd/model/User.java
package com.ibnrochd.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef; // Important pour les références
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.HashSet; // Ajout
import java.util.Date;

/**
 * Modèle représentant un utilisateur (Étudiant, Professeur, Admin).
 * Le nom de la collection MongoDB sera "users".
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "Format d'email invalide")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @DBRef // Stocke une référence aux documents Role plutôt que de les embarquer
    private Set<Role> roles = new HashSet<>(); // Initialiser pour éviter NullPointerException

    private boolean active = true;

    private Date dateCreation = new Date();
    private Date dateMiseAJour;

    public User(String prenom, String nom, String email, String motDePasse) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.dateCreation = new Date();
    }
}