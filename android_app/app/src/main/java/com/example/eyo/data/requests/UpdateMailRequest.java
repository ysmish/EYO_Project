package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateMailRequest implements ApiRequest<String> {
    
    private final int mailId;
    private final String authToken;
    private final Boolean reportSpam;
    private final List<String> labels;
    private final Boolean read;
    
    // Constructor for spam reporting
    public UpdateMailRequest(int mailId, boolean reportSpam, String authToken) {
        this.mailId = mailId;
        this.reportSpam = reportSpam;
        this.authToken = authToken;
        this.labels = null;
        this.read = null;
    }
    
    // Constructor for label updates
    public UpdateMailRequest(int mailId, List<String> labels, String authToken) {
        this.mailId = mailId;
        this.labels = labels;
        this.authToken = authToken;
        this.reportSpam = null;
        this.read = null;
    }
    
    // Constructor for read status update
    public UpdateMailRequest(int mailId, boolean read, String authToken, boolean isReadUpdate) {
        this.mailId = mailId;
        this.read = read;
        this.authToken = authToken;
        this.reportSpam = null;
        this.labels = null;
    }
    
    @Override
    public String getEndpoint() {
        return "/mails/" + mailId;
    }
    
    @Override
    public String getMethod() {
        return "PATCH";
    }
    
    @Override
    public String getRequestBody() {
        try {
            JSONObject jsonPayload = new JSONObject();
            
            if (reportSpam != null) {
                jsonPayload.put("reportSpam", reportSpam);
            }
            
            if (labels != null) {
                JSONArray labelsArray = new JSONArray();
                for (String label : labels) {
                    // Try to parse as integer first, if it fails add as string
                    try {
                        int labelId = Integer.parseInt(label);
                        labelsArray.put(labelId);
                    } catch (NumberFormatException e) {
                        labelsArray.put(label);
                    }
                }
                jsonPayload.put("labels", labelsArray);
            }
            
            if (read != null) {
                jsonPayload.put("read", read);
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
        if (responseCode == 200 || responseCode == 204) {
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.optString("message", "Mail updated successfully");
            } catch (JSONException e) {
                return "Mail updated successfully";
            }
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Failed to update mail with code: " + responseCode);
        } catch (JSONException e) {
            return "Failed to update mail with code: " + responseCode;
        }
    }
}
