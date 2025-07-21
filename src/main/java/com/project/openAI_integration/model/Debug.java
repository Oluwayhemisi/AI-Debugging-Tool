package com.project.openAI_integration.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Debug {


    private String userInput;

    private String gptResponse;

    private String operationType;

    private LocalDateTime timestamp;
}
