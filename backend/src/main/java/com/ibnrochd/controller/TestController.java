// src/main/java/com/ibnrochd/controller/TestController.java
package com.ibnrochd.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize; // Pour la sécurité basée sur les rôles

/**
 * Un contrôleur de test simple pour vérifier que l'application fonctionne.
 * Et pour tester les accès basés sur les rôles.
 */
@RestController
@RequestMapping("/api/test") // Préfixe commun pour les endpoints de ce contrôleur
public class TestController {

    /**
     * Endpoint public accessible à tous.
     * @return Un message de bienvenue.
     */
    @GetMapping("/all")
    public String allAccess() {
        return "Contenu public. Bienvenue sur l'application Ibn Rochd !";
    }

    /**
     * Endpoint accessible uniquement aux utilisateurs authentifiés (tous rôles).
     * @return Un message pour les utilisateurs connectés.
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('ETUDIANT') or hasRole('PROFESSEUR') or hasRole('ADMIN')")
    public String userAccess() {
        return "Contenu pour les utilisateurs connectés (Étudiants, Professeurs, Admins).";
    }

    /**
     * Endpoint accessible uniquement aux utilisateurs avec le rôle ETUDIANT.
     * @return Un message pour les étudiants.
     */
    @GetMapping("/etudiant")
    @PreAuthorize("hasRole('ETUDIANT')")
    public String studentAccess() {
        return "Tableau de bord Étudiant.";
    }

    /**
     * Endpoint accessible uniquement aux utilisateurs avec le rôle PROFESSEUR.
     * @return Un message pour les professeurs.
     */
    @GetMapping("/professeur")
    @PreAuthorize("hasRole('PROFESSEUR')")
    public String teacherAccess() {
        return "Tableau de bord Professeur.";
    }

    /**
     * Endpoint accessible uniquement aux utilisateurs avec le rôle ADMIN.
     * @return Un message pour les administrateurs.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Tableau de bord Administrateur.";
    }
}