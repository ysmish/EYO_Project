package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckUserRequest implements ApiRequest<Boolean> {
    
    private final String username;
    private final String authToken;
    
    public CheckUserRequest(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }
    
    @Override
    public String getEndpoint() {
        return "/users/" + username;
    }
    
    @Override
    public String getMethod() {
        return "GET";
    }
    
    @Override
    public String getRequestBody() {
        return "";
    }
    
    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (authToken != null && !authToken.isEmpty()) {
            headers.put("Authorization", authToken);
        }
        return headers;
    }
    
    @Override
    public Boolean parseResponse(int responseCode, String responseBody) {
        // If we get a 200, the user exists
        return responseCode == 200;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        if (responseCode == 404) {
            return "User '" + username + "' not found";
        }
        return "Error checking user: " + responseCode;
    }
} 