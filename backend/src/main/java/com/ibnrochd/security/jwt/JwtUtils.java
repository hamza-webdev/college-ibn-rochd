// src/main/java/com/ibnrochd/security/jwt/JwtUtils.java
package com.ibnrochd.security.jwt;

import com.ibnrochd.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Classe utilitaire pour la gestion des tokens JWT (création, validation, extraction d'informations).
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${ibnrochd.app.jwtSecret}") // Clé secrète pour signer les tokens JWT
    private String jwtSecret;

    @Value("${ibnrochd.app.jwtExpirationMs}") // Durée de validité d'un token JWT en millisecondes
    private int jwtExpirationMs;

    /**
     * Génère un token JWT à partir des informations d'authentification de l'utilisateur.
     * @param authentication L'objet Authentication contenant les détails de l'utilisateur.
     * @return Le token JWT généré.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Utilise l'email comme sujet
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Retourne la clé de signature à partir de la clé secrète.
     * @return La clé de signature.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extrait le nom d'utilisateur (email) du token JWT.
     * @param token Le token JWT.
     * @return Le nom d'utilisateur (email).
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Valide un token JWT.
     * @param authToken Le token JWT à valider.
     * @return true si le token est valide, false sinon.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT invalide: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Les claims JWT sont vides: {}", e.getMessage());
        }
        return false;
    }
}