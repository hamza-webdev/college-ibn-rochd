// src/main/java/com/ibnrochd/controller/AuthController.java
package com.ibnrochd.controller;

import com.ibnrochd.dto.request.LoginRequest;
import com.ibnrochd.dto.request.SignupRequest;
import com.ibnrochd.dto.response.UserInfoResponse;
import com.ibnrochd.dto.response.MessageResponse;
import com.ibnrochd.model.ERole;
import com.ibnrochd.model.Role;
import com.ibnrochd.model.User;
import com.ibnrochd.repository.RoleRepository;
import com.ibnrochd.repository.UserRepository;
import com.ibnrochd.security.jwt.JwtUtils;
import com.ibnrochd.security.services.UserDetailsImpl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contrôleur pour gérer les opérations d'authentification (connexion, inscription).
 */
@CrossOrigin(origins = "*", maxAge = 3600) // Permet les requêtes Cross-Origin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Endpoint pour la connexion des utilisateurs.
     * @param loginRequest Les informations de connexion (email, mot de passe).
     * @return Une réponse HTTP avec les informations de l'utilisateur et le token JWT.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getMotDePasse()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateJwtToken(authentication); // Génère le token JWT

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserInfoResponse(userDetails.getId(),
                userDetails.getPrenom(),
                userDetails.getNom(),
                userDetails.getEmail(),
                roles,
                jwtToken)); // Retourne le token dans la réponse JSON
    }

    /**
     * Endpoint pour l'inscription de nouveaux utilisateurs.
     * @param signUpRequest Les informations d'inscription.
     * @return Une réponse HTTP indiquant le succès ou l'échec de l'inscription.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Cet email est déjà utilisé!"));
        }

        // Créer un nouvel utilisateur
        User user = new User(signUpRequest.getPrenom(),
                signUpRequest.getNom(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getMotDePasse()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_ETUDIANT) // Rôle par défaut: ETUDIANT
                    .orElseThrow(() -> new RuntimeException("Erreur: Rôle ETUDIANT non trouvé."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle ADMIN non trouvé."));
                        roles.add(adminRole);
                        break;
                    case "prof":
                    case "professeur":
                        Role modRole = roleRepository.findByName(ERole.ROLE_PROFESSEUR)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle PROFESSEUR non trouvé."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_ETUDIANT)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle ETUDIANT non trouvé."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès!"));
    }
}