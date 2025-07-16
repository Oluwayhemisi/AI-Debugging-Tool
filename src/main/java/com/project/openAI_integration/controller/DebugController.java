package com.project.openAI_integration.controller;

import com.project.openAI_integration.dto.DebugRequest;
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
    public Debug analyzeCode(@RequestBody DebugRequest debugRequest) {
        return gptService.processPrompt(debugRequest.getCode(), debugRequest.getCustomInstruction());
    }
}
