// src/main/java/com/ibnrochd/dto/response/MessageResponse.java
package com.ibnrochd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO pour les messages de réponse génériques.
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
}