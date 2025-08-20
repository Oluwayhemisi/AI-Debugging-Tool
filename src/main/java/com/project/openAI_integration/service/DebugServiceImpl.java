package com.project.openAI_integration.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;    private static final Logger log = LoggerFactory.getLogger(DebugServiceImpl.class);




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
    public Debug processPrompt(String prompt) {
        // ✅ Build request payload
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");
        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.7);

        // ✅ Create HTTP request
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(gson.toJson(requestBody), JSON))
                .build();

        log.info("Sending request to URL: {}", request.url());
        log.debug("Request headers: {}", request.headers());
        log.debug("Request body: {}", gson.toJson(requestBody));

        String gptResponse;
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (response.isSuccessful()) {
                JsonObject json = gson.fromJson(responseBody, JsonObject.class);
                gptResponse = json.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();

                log.info("Received successful response");
            } else {
                gptResponse = "Error: " + responseBody;
                log.error("Request failed. Code: {}, Message: {}, Body: {}",
                        response.code(), response.message(), responseBody);
            }
        } catch (IOException e) {
            gptResponse = "IOException: " + e.getMessage();
            log.error("IOException while calling OpenAI API", e);
        }

        // ✅ Build Debug session response
        Debug session = new Debug();
        session.setGptResponse(gptResponse);
        return session;
    }

}
