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
    private Date dateMiseAJour = new Date();

    private Date dateDeNaissance;
    // private Adresse adresse; // Si vous avez une classe Adresse

    private DetailsEtudiant detailsEtudiant;
    private DetailsProfesseur detailsProfesseur; // Si vous avez une classe DetailsProfesseur

    // private List<TokenAppareilMobile> tokensAppareilMobile; // Si pertinent

    private boolean active = true;
    private Date dateCreation;
    private Date dateMiseAJour;

    // Classe interne statique pour les détails spécifiques à un étudiant
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailsEtudiant {
        private String matriculeEtudiant;
        private String dateInscription; // Envisagez java.time.LocalDate pour une meilleure gestion
        private String niveauActuel;
        private String etablissementPrecedent;
        private String idClasse; // Champ crucial pour la liaison avec ClassGroup
    }

    // Classe interne statique pour les détails spécifiques à un professeur (exemple)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailsProfesseur {
        private String matriculeProfesseur;
        private String specialisation;
        private Date dateEmbauche; // Envisagez java.time.LocalDate
        // Autres champs...
    }

    // Constructeur personnalisé si nécessaire, par exemple pour l'enregistrement initial
    public User(String prenom, String nom, String email, String motDePasse) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.active = true;
        this.dateCreation = new Date();
        this.dateMiseAJour = new Date();
        this.roles = new HashSet<>(); // Important d'initialiser
        // Il est généralement préférable d'initialiser detailsEtudiant et detailsProfesseur
        // uniquement lorsque l'utilisateur a le rôle correspondant, ou de les laisser null.
        // Par exemple, lors de la création d'un étudiant :
        // if (this.roles.stream().anyMatch(role -> role.getName() == ERole.ROLE_ETUDIANT)) {
        //     this.detailsEtudiant = new DetailsEtudiant();
        // }
    }
}