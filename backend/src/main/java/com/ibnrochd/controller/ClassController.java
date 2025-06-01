package com.ibnrochd.controller;

import com.ibnrochd.dto.request.ClassGroupRequest;
import com.ibnrochd.dto.response.ClassGroupResponse;
import com.ibnrochd.dto.response.MessageResponse;
import com.ibnrochd.exception.ResourceNotFoundException;
import com.ibnrochd.model.ClassGroup;
import com.ibnrochd.service.ClassGroupService;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassGroupService classGroupService;

    @Autowired
    private ModelMapper modelMapper; // Assurez-vous d'avoir ModelMapper configuré comme Bean

    private ClassGroupResponse convertToResponse(ClassGroup classGroup) {
        return modelMapper.map(classGroup, ClassGroupResponse.class);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassGroupResponse> createClassGroup(@Valid @RequestBody ClassGroupRequest classGroupRequest) {
        ClassGroup newClassGroup = classGroupService.createClassGroup(classGroupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(newClassGroup));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR') or hasRole('ETUDIANT')") // Ajuster selon les besoins
    public ResponseEntity<ClassGroupResponse> getClassGroupById(@PathVariable String id) {
        return classGroupService.getClassGroupById(id)
                .map(cg -> ResponseEntity.ok(convertToResponse(cg)))
                .orElseThrow(() -> new ResourceNotFoundException("Classe non trouvée avec l'id: " + id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSEUR')")
    public ResponseEntity<List<ClassGroupResponse>> getAllClassGroups(@RequestParam(required = false) String anneeScolaire) {
        List<ClassGroup> classGroups;
        if (anneeScolaire != null && !anneeScolaire.isEmpty()) {
            classGroups = classGroupService.getClassGroupsByAnneeScolaire(anneeScolaire);
        } else {
            classGroups = classGroupService.getAllClassGroups();
        }
        List<ClassGroupResponse> responses = classGroups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassGroupResponse> updateClassGroup(@PathVariable String id, @Valid @RequestBody ClassGroupRequest classGroupRequest) {
        try {
            ClassGroup updatedClassGroup = classGroupService.updateClassGroup(id, classGroupRequest);
            return ResponseEntity.ok(convertToResponse(updatedClassGroup));
        } catch (ResourceNotFoundException e) {
            throw e; // Laisser GlobalExceptionHandler gérer
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteClassGroup(@PathVariable String id) {
        try {
            classGroupService.deleteClassGroup(id);
            return ResponseEntity.ok(new MessageResponse("Classe supprimée avec succès!"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    // Endpoints pour gérer les étudiants dans une classe
    @PostMapping("/{classId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassGroupResponse> addStudentToClass(@PathVariable String classId, @PathVariable String studentId) {
        ClassGroup updatedClassGroup = classGroupService.addStudentToClass(classId, studentId);
        return ResponseEntity.ok(convertToResponse(updatedClassGroup));
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassGroupResponse> removeStudentFromClass(@PathVariable String classId, @PathVariable String studentId) {
        ClassGroup updatedClassGroup = classGroupService.removeStudentFromClass(classId, studentId);
        return ResponseEntity.ok(convertToResponse(updatedClassGroup));
    }
}