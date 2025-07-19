package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveDraftRequest implements ApiRequest<String> {
    
    private final List<String> toList;
    private final List<String> ccList;
    private final String subject;
    private final String body;
    private final String authToken;
    
    public SaveDraftRequest(List<String> toList, List<String> ccList, String subject, String body, String authToken) {
        this.toList = toList;
        this.ccList = ccList;
        this.subject = subject;
        this.body = body;
        this.authToken = authToken;
    }
    
    @Override
    public String getEndpoint() {
        return "/mails/drafts";
    }
    
    @Override
    public String getMethod() {
        return "POST";
    }
    
    @Override
    public String getRequestBody() {
        try {
            JSONObject jsonPayload = new JSONObject();
            
            // Add To field - convert to array
            JSONArray toArray = new JSONArray();
            if (toList != null) {
                for (String user : toList) {
                    if (user != null && !user.trim().isEmpty()) {
                        toArray.put(user.trim());
                    }
                }
            }
            jsonPayload.put("to", toArray);
            
            // Add CC field - convert to array  
            JSONArray ccArray = new JSONArray();
            if (ccList != null) {
                for (String user : ccList) {
                    if (user != null && !user.trim().isEmpty()) {
                        ccArray.put(user.trim());
                    }
                }
            }
            jsonPayload.put("cc", ccArray);
            
            // Add Subject
            jsonPayload.put("subject", subject != null ? subject.trim() : "");
            
            // Add Body
            jsonPayload.put("body", body != null ? body.trim() : "");
            
            // Add empty attachments array
            jsonPayload.put("attachments", new JSONArray());
            
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
        if (responseCode == 201 || responseCode == 200) {
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.optString("message", "Draft saved successfully");
            } catch (JSONException e) {
                return "Draft saved successfully";
            }
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Failed to save draft with code: " + responseCode);
        } catch (JSONException e) {
            return "Failed to save draft with code: " + responseCode;
        }
    }
} 