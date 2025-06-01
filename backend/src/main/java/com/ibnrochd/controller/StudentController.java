package com.ibnrochd.controller;

import com.ibnrochd.model.User;
import com.ibnrochd.service.StudentService;
import com.ibnrochd.dto.request.SignupRequest; // Reusing for creation/update
import com.ibnrochd.dto.response.UserInfoResponse; 
import com.ibnrochd.dto.response.MessageResponse;
import com.ibnrochd.exception.ResourceNotFoundException;
import com.ibnrochd.security.services.UserDetailsImpl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    private UserInfoResponse convertToUserInfoResponse(User user) {
        List<String> roles = user.getRoles().stream()
                                 .map(role -> role.getName().name())
                                 .collect(Collectors.toList());
        // Token is not applicable here or can be set to null for general info
        return new UserInfoResponse(user.getId(), user.getPrenom(), user.getNom(), user.getEmail(), roles, null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createStudent(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            User newStudent = studentService.createStudent(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToUserInfoResponse(newStudent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Erreur lors de la création de l'étudiant: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<List<UserInfoResponse>> getAllStudents() {
        List<User> students = studentService.getAllStudents();
        List<UserInfoResponse> studentResponses = students.stream()
                .map(this::convertToUserInfoResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentResponses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Further checks inside the method
    public ResponseEntity<?> getStudentById(@PathVariable String id, Authentication authentication) {
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = currentUserDetails.getId();
        boolean isAdmin = currentUserDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isProfesseur = currentUserDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PROFESSEUR"));

        try {
            User student = studentService.getStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'id: " + id));

            // Authorization: Admin, Professor, or the student themselves
            if (isAdmin || isProfesseur || currentUserId.equals(student.getId())) {
                return ResponseEntity.ok(convertToUserInfoResponse(student));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Accès refusé."));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only Admin can update student info for now
    public ResponseEntity<?> updateStudent(@PathVariable String id, @Valid @RequestBody SignupRequest signupRequest) {
        try {
            User updatedStudent = studentService.updateStudent(id, signupRequest);
            return ResponseEntity.ok(convertToUserInfoResponse(updatedStudent));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok(new MessageResponse("Étudiant supprimé avec succès!"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }
}