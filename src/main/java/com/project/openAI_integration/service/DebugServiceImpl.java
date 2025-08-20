package com.project.openAI_integration.service;

import com.project.openAI_integration.model.Debug;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DebugServiceImpl implements DebugService{

    private final OkHttpClient client;
    private static final Logger log = LoggerFactory.getLogger(DebugServiceImpl.class);




    @Value("${openai.api.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        if (apiKey != null) {
            log.info("Loaded API KEY: ✅ Loaded");
        } else {
            log.error("Loaded API KEY: ❌ MISSING");
        }    }

    @Override
    public Debug processPrompt(String code, String customInstruction) {
        String prompt = customInstruction + "\n\n" + code;

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new JSONArray().put(message));
        requestBody.put("temperature", 0.7);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                .build();

        log.info("Sending request to URL: {}", request.url());
        log.debug("Request headers: {}", request.headers());
        log.debug("Request body: {}", requestBody.toString(2));

        String gptResponse = "";
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                JSONObject json = new JSONObject(result);
                gptResponse = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                log.info("Received successful response: {}", gptResponse);

            } else {
                assert response.body() != null;
                gptResponse = "Error: " + response.body().string();
                log.error("Request failed. Code: {}, Message: {}, Body: {}",
                        response.code(), response.message(), response.body().string());
            }
        } catch (IOException e) {
            gptResponse = "IOException: " + e.getMessage();
        }

        Debug session = new Debug();
        session.setUserInput(code);
        session.setGptResponse(gptResponse);
        session.setOperationType(customInstruction); // now stores user instruction
        session.setTimestamp(LocalDateTime.now());

        return session;
    }


    private String buildPrompt(String code, String operation) {
        switch (operation.toLowerCase()) {
            case "fix": return "Fix the bugs in the following code:\n\n" + code;
            case "explain": return "Explain what the following code does:\n\n" + code;
            case "refactor": return "Refactor the following code to be cleaner and more efficient:\n\n" + code;
            default: return "Analyze the following code:\n\n" + code;
        }
    }
}
