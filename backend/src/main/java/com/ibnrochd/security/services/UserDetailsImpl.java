// src/main/java/com/ibnrochd/security/services/UserDetailsImpl.java
package com.ibnrochd.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibnrochd.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implémentation de UserDetails pour Spring Security.
 * Contient les informations de l'utilisateur nécessaires à l'authentification et à l'autorisation.
 */
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String id;
    private String prenom;
    private String nom;
    private String email;

    @JsonIgnore // Empêche la sérialisation du mot de passe dans les réponses JSON
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String prenom, String nom, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Construit un UserDetailsImpl à partir d'un objet User.
     * @param user L'objet User de la base de données.
     * @return Une instance de UserDetailsImpl.
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getPrenom(),
                user.getNom(),
                user.getEmail(),
                user.getMotDePasse(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPrenom() { return prenom; }

    public String getNom() { return nom; }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Utilise l'email comme nom d'utilisateur pour Spring Security
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // Peut être lié au champ 'active' de l'entité User
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}