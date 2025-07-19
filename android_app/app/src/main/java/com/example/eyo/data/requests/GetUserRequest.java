package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import com.example.eyo.data.User;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetUserRequest implements ApiRequest<User> {
    
    private final String username;
    private final String authToken;
    
    public GetUserRequest(String username, String authToken) {
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
    public User parseResponse(int responseCode, String responseBody) {
        if (responseCode == 200) {
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                User user = new User();
                user.setUsername(jsonResponse.getString("id"));
                user.setFirstName(jsonResponse.getString("firstName"));
                user.setLastName(jsonResponse.getString("lastName"));
                user.setBirthday(jsonResponse.getString("birthday"));
                user.setPhoto(jsonResponse.optString("photo", null));
                return user;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse user data from response: " + e.getMessage());
            }
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        if (responseCode == 404) {
            return "User '" + username + "' not found";
        } else if (responseCode == 401) {
            return "Authentication required";
        }
        return "Error fetching user data: " + responseCode;
    }
} 