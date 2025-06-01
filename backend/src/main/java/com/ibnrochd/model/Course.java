package com.ibnrochd.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    private String id;

    private String nomCours; // Ex: "Mathématiques", "Histoire-Géographie"
    private String codeCours; // Ex: "MATH01", "HISTGEO01" (Optionnel, mais peut être utile)
    private String description; // Optionnel

    private Date creeLe;
    private Date misAJourLe;
}