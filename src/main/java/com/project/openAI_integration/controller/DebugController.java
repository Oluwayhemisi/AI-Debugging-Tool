package com.project.openAI_integration.controller;

import com.project.openAI_integration.dto.DebugRequest;
import com.project.openAI_integration.model.Debug;
import com.project.openAI_integration.service.DebugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {
    @Autowired
    private DebugService gptService;

    @Value("${openai.api.key}")
    private String apiKey;

    @PostMapping("/analyze")
    public Debug analyzeCode(@RequestBody DebugRequest debugRequest) {
        return gptService.processPrompt(debugRequest.getPrompt());
    }
    @GetMapping("/test-key")
    public ResponseEntity<String> testKey() {
        return ResponseEntity.ok("Key is: " + (apiKey != null ? apiKey.substring(0, 5) + "..." : "MISSING"));
    }

}
