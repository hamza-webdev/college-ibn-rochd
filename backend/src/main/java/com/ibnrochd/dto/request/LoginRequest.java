package com.ibnrochd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String email; // Assurez-vous que ce nom correspond à getEmail()

    @NotBlank
    private String motDePasse; // Assurez-vous que ce nom correspond à getMotDePasse()
}