package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONObject;

public class LoginRequest implements ApiRequest<String> {
    private String username;
    private String password;
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Override
    public String getEndpoint() {
        return "/tokens";
    }
    
    @Override
    public String getMethod() {
        return "POST";
    }
    
    @Override
    public String getRequestBody() {
        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("username", username);
            jsonPayload.put("password", password);
            return jsonPayload.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public String parseResponse(int responseCode, String responseBody) {
        if (responseCode == 201) { // HTTP_CREATED
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getString("token");
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse token from response: " + e.getMessage());
            }
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            String error = jsonResponse.optString("error");
            return error.isEmpty() ? "Invalid username or password" : error;
        } catch (Exception e) {
            return "Invalid username or password";
        }
    }
} 