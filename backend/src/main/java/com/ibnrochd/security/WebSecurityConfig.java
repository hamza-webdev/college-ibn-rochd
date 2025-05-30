// src/main/java/com/ibnrochd/security/WebSecurityConfig.java
package com.ibnrochd.security;

import com.ibnrochd.security.jwt.AuthEntryPointJwt;
import com.ibnrochd.security.jwt.AuthTokenFilter;
import com.ibnrochd.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration principale de Spring Security.
 * Définit le PasswordEncoder, l'AuthenticationManager, et la chaîne de filtres de sécurité.
 */
@Configuration
@EnableMethodSecurity // Permet d'utiliser @PreAuthorize sur les méthodes des contrôleurs
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * Crée le bean pour le filtre d'authentification JWT.
     * @return Le filtre AuthTokenFilter.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Crée le bean pour le DaoAuthenticationProvider.
     * Ce provider utilise UserDetailsService et PasswordEncoder pour authentifier les utilisateurs.
     * @return Le DaoAuthenticationProvider configuré.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Crée le bean pour l'AuthenticationManager.
     * @param authConfig La configuration d'authentification.
     * @return L'AuthenticationManager.
     * @throws Exception Si une erreur survient lors de la récupération de l'AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Crée le bean pour le PasswordEncoder (BCrypt).
     * @return Le PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure la chaîne de filtres de sécurité HTTP.
     * Définit les règles d'autorisation pour les endpoints, la gestion des sessions, CORS, CSRF.
     * @param http L'objet HttpSecurity à configurer.
     * @return La SecurityFilterChain construite.
     * @throws Exception Si une erreur de configuration survient.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Désactive CSRF car JWT est utilisé
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Gère les erreurs d'authentification
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sessions stateless
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Endpoints d'authentification publics
                                .requestMatchers("/api/test/all").permitAll() // Endpoint de test public
                                .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                );

        http.authenticationProvider(authenticationProvider()); // Ajoute le provider d'authentification

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Ajoute le filtre JWT

        return http.build();
    }
}