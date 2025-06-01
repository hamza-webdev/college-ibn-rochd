package com.ibnrochd.service;

import com.ibnrochd.model.User;
import com.ibnrochd.model.Role;
import com.ibnrochd.model.ERole;
import com.ibnrochd.repository.UserRepository;
import com.ibnrochd.repository.RoleRepository;
import com.ibnrochd.dto.request.SignupRequest;
import com.ibnrochd.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createStudent(SignupRequest studentRequest) {
        if (userRepository.existsByEmail(studentRequest.getEmail())) {
            throw new IllegalArgumentException("Erreur: Cet email est déjà utilisé!");
        }

        User student = new User(
                studentRequest.getPrenom(),
                studentRequest.getNom(),
                studentRequest.getEmail(),
                passwordEncoder.encode(studentRequest.getMotDePasse())
        );

        Role studentRole = roleRepository.findByName(ERole.ROLE_ETUDIANT)
                .orElseThrow(() -> new RuntimeException("Erreur: Rôle ETUDIANT non trouvé. Veuillez initialiser les rôles."));
        
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        student.setRoles(roles);
        student.setActive(true); // Default to active
        student.setDateCreation(new Date());

        return userRepository.save(student);
    }

    public List<User> getAllStudents() {
        return userRepository.findByRoles_Name(ERole.ROLE_ETUDIANT);
    }

    public Optional<User> getStudentById(String id) {
        return userRepository.findById(id)
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_ETUDIANT));
    }
    
    public User updateStudent(String id, SignupRequest studentDetails) {
        User student = getStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'id: " + id));

        if (!student.getEmail().equals(studentDetails.getEmail()) && userRepository.existsByEmail(studentDetails.getEmail())) {
            throw new IllegalArgumentException("Erreur: Le nouvel email est déjà utilisé par un autre compte!");
        }

        student.setPrenom(studentDetails.getPrenom());
        student.setNom(studentDetails.getNom());
        student.setEmail(studentDetails.getEmail());
        if (studentDetails.getMotDePasse() != null && !studentDetails.getMotDePasse().trim().isEmpty()) {
            student.setMotDePasse(passwordEncoder.encode(studentDetails.getMotDePasse()));
        }
        student.setDateMiseAJour(new Date());
        return userRepository.save(student);
    }

    public void deleteStudent(String id) {
        User student = getStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'id: " + id));
        userRepository.delete(student);
    }
}