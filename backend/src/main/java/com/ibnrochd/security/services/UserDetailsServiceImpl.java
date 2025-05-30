// src/main/java/com/ibnrochd/security/services/UserDetailsServiceImpl.java
package com.ibnrochd.security.services;

import com.ibnrochd.model.User;
import com.ibnrochd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour charger les détails de l'utilisateur pour Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    /**
     * Charge un utilisateur par son nom d'utilisateur (qui est l'email dans cette application).
     * @param email L'email de l'utilisateur.
     * @return Un objet UserDetails contenant les informations de l'utilisateur.
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé.
     */
    @Override
    @Transactional // Assure que les données (notamment les rôles) sont chargées correctement
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        return UserDetailsImpl.build(user);
    }
}