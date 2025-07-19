package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateLabelRequest implements ApiRequest<String> {
    private String authToken;
    private String name;
    private String color;

    public CreateLabelRequest(String name, String color, String authToken) {
        this.name = name;
        this.color = color;
        this.authToken = authToken;
    }

    @Override
    public String getEndpoint() {
        return "/labels";
    }

    @Override
    public String getMethod() {
        return "POST";
    }

    @Override
    public String getRequestBody() {
        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("name", name);
            if (color != null && !color.isEmpty()) {
                jsonPayload.put("color", color);
            } else {
                jsonPayload.put("color", "#4F46E5"); // Default color
            }
            return jsonPayload.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Failed to create JSON payload: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (authToken != null && !authToken.isEmpty()) {
            headers.put("Authorization", authToken);
        }
        headers.put("Content-Type", "application/json");
        return headers;
    }

    @Override
    public String parseResponse(int responseCode, String responseBody) {
        if (responseCode == 201) {
            return "Label created successfully";
        }
        return null;
    }

    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Failed to create label with code: " + responseCode);
        } catch (JSONException e) {
            return "Failed to create label with code: " + responseCode;
        }
    }
} 