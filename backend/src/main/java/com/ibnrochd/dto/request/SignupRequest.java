package com.ibnrochd.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String prenom; // Correspond à getPrenom()

    @NotBlank
    @Size(min = 2, max = 50)
    private String nom; // Correspond à getNom()

    @NotBlank
    @Size(max = 50)
    @Email
    private String email; // Correspond à getEmail()

    private Set<String> role; // Correspond à getRole()

    @NotBlank
    @Size(min = 6, max = 40)
    private String motDePasse; // Correspond à getMotDePasse()
}