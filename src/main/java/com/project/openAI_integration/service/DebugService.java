package com.project.openAI_integration.service;

import com.project.openAI_integration.model.Debug;

public interface DebugService {
    Debug processPrompt(String code, String customInstruction);
}
