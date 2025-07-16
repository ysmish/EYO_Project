package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import com.example.eyo.data.User;
import org.json.JSONObject;

public class RegisterRequest implements ApiRequest<String> {
    private User user;
    
    public RegisterRequest(User user) {
        this.user = user;
    }
    
    @Override
    public String getEndpoint() {
        return "/users";
    }
    
    @Override
    public String getMethod() {
        return "POST";
    }
    
    @Override
    public String getRequestBody() {
        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("firstName", user.getFirstName());
            jsonPayload.put("lastName", user.getLastName());
            jsonPayload.put("birthday", user.getBirthday());
            jsonPayload.put("username", user.getUsername());
            jsonPayload.put("password", user.getPassword());
            if (user.getPhoto() != null) {
                jsonPayload.put("photo", user.getPhoto());
            }
            return jsonPayload.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public String parseResponse(int responseCode, String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.optString("message", "User registered successfully");
        } catch (Exception e) {
            return "User registered successfully";
        }
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            String error = jsonResponse.optString("error");
            if (error.isEmpty()) {
                error = jsonResponse.optString("message", "Registration failed");
            }
            return error;
        } catch (Exception e) {
            return "Registration failed";
        }
    }
} 