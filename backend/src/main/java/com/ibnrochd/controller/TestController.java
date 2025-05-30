// src/main/java/com/ibnrochd/controller/TestController.java
package com.ibnrochd.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Un contrôleur de test simple pour vérifier que l'application fonctionne.
 * Et pour tester les accès basés sur les rôles.
 */
@CrossOrigin(origins = "*", maxAge = 3600) // Permet les requêtes Cross-Origin
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Contenu public. Bienvenue sur l'application Ibn Rochd !";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ETUDIANT') or hasRole('PROFESSEUR') or hasRole('ADMIN')")
    public String userAccess() {
        return "Contenu pour les utilisateurs connectés (Étudiants, Professeurs, Admins).";
    }

    @GetMapping("/etudiant")
    @PreAuthorize("hasRole('ETUDIANT')")
    public String studentAccess() {
        return "Tableau de bord Étudiant.";
    }

    @GetMapping("/professeur")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public String teacherAccess() {
        return "Tableau de bord Professeur.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Tableau de bord Administrateur.";
    }
}