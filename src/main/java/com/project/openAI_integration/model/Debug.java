package com.project.openAI_integration.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Debug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userInput;

    @Lob
    private String gptResponse;

    private String operationType; // e.g., "explain", "fix", "refactor"

    private LocalDateTime timestamp;
}
