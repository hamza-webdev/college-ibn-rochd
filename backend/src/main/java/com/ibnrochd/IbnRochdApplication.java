// src/main/java/com/ibnrochd/IbnRochdApplication.java
package com.ibnrochd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Ajout pour CommandLineRunner
import com.ibnrochd.model.ERole;
import com.ibnrochd.model.Role; 
import com.ibnrochd.repository.RoleRepository; 
import org.springframework.boot.CommandLineRunner; 

/**
 * Classe principale de l'application Spring Boot Ibn Rochd.
 * Point d'entrée de l'application.
 */
@SpringBootApplication
public class IbnRochdApplication {

    public static void main(String[] args) {
        SpringApplication.run(IbnRochdApplication.class, args);
    }

    /**
     * Bean pour initialiser les rôles dans la base de données au démarrage si nécessaire.
     * @param roleRepository Le repository pour les rôles.
     * @return Un CommandLineRunner.
     */
    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName(ERole.ROLE_ETUDIANT).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_ETUDIANT));
                System.out.println("Rôle ETUDIANT initialisé.");
            }
            if (roleRepository.findByName(ERole.ROLE_PROFESSEUR).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_PROFESSEUR));
                System.out.println("Rôle PROFESSEUR initialisé.");
            }
            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_ADMIN));
                System.out.println("Rôle ADMIN initialisé.");
            }
        };
    }
}