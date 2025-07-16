package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONObject;

public class LoginRequest implements ApiRequest<Boolean> {
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
    public Boolean parseResponse(int responseCode, String responseBody) {
        return responseCode == 201; // HTTP_CREATED
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