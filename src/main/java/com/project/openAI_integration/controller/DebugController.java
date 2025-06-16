package com.project.openAI_integration.controller;

import com.project.openAI_integration.model.Debug;
import com.project.openAI_integration.service.DebugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class DebugController {
    @Autowired
    private DebugService gptService;

    @PostMapping("/analyze")
    public Debug analyzeCode(@RequestParam("code") String code,
                             @RequestParam("operation") String operation) {
        return gptService.processPrompt(code, operation);
    }
}
