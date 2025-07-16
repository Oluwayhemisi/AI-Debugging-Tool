package com.project.openAI_integration.service;

import com.project.openAI_integration.model.Debug;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DebugServiceImpl implements DebugService{

    private final OkHttpClient client;



    @Value("${openai.api.key}")
    private String apiKey;


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

        String gptResponse = "";
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                JSONObject json = new JSONObject(result);
                gptResponse = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                gptResponse = "Error: " + response.code();
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
