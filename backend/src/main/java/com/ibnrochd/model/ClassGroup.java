package com.ibnrochd.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "classgroups") // Nom de la collection MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassGroup {

    @Id
    private String id;

    private String nomClasse; // Ex: "1ère A", "4ème B"
    private String niveauScolaire; // Ex: "Niveau 1", "Première Année du Collège"
    private String anneeScolaire; // Ex: "2024-2025"

    private String idProfesseurPrincipal; // Référence à un utilisateur (professeur)

    private Set<String> listeEtudiantsIds = new HashSet<>(); // Liste des IDs des étudiants dans cette classe

    private Integer capaciteMax = 20; // Capacité maximale de la classe

    private Set<String> listeCoursIds = new HashSet<>(); // Liste des IDs des cours suivis par cette classe

    private String sallePrincipale; // Optionnel

    private Date creeLe;
    private Date misAJourLe;

    // Constructeur pour la création, sans IDs auto-générés ni dates
    public ClassGroup(String nomClasse, String niveauScolaire, String anneeScolaire, String idProfesseurPrincipal, Integer capaciteMax, String sallePrincipale) {
        this.nomClasse = nomClasse;
        this.niveauScolaire = niveauScolaire;
        this.anneeScolaire = anneeScolaire;
        this.idProfesseurPrincipal = idProfesseurPrincipal;
        if (capaciteMax != null) {
            this.capaciteMax = capaciteMax;
        }
        this.sallePrincipale = sallePrincipale;
    }

    // Méthodes utilitaires pour gérer les listes d'IDs si nécessaire
    public void addEtudiant(String etudiantId) {
        this.listeEtudiantsIds.add(etudiantId);
    }

    public void removeEtudiant(String etudiantId) {
        this.listeEtudiantsIds.remove(etudiantId);
    }
    // ... autres méthodes pour coursIds
}