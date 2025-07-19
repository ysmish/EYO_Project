package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendDraftRequest implements ApiRequest<String> {
    
    private final int draftId;
    private final List<String> toList;
    private final List<String> ccList;
    private final String subject;
    private final String body;
    private final String authToken;
    
    public SendDraftRequest(int draftId, List<String> toList, List<String> ccList, String subject, String body, String authToken) {
        this.draftId = draftId;
        this.toList = toList;
        this.ccList = ccList;
        this.subject = subject;
        this.body = body;
        this.authToken = authToken;
    }
    
    @Override
    public String getEndpoint() {
        return "/mails/drafts/" + draftId + "/send";
    }
    
    @Override
    public String getMethod() {
        return "POST";
    }
    
    @Override
    public String getRequestBody() {
        try {
            JSONObject jsonPayload = new JSONObject();
            
            // Add To field
            JSONArray toArray = new JSONArray();
            if (toList != null) {
                for (String recipient : toList) {
                    toArray.put(recipient);
                }
            }
            jsonPayload.put("to", toArray);
            
            // Add CC field
            JSONArray ccArray = new JSONArray();
            if (ccList != null) {
                for (String recipient : ccList) {
                    ccArray.put(recipient);
                }
            }
            jsonPayload.put("cc", ccArray);
            
            // Add Subject
            jsonPayload.put("subject", subject != null ? subject : "");
            
            // Add Body
            jsonPayload.put("body", body != null ? body : "");
            
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
        return headers;
    }
    
    @Override
    public String parseResponse(int responseCode, String responseBody) {
        if (responseCode == 201 || responseCode == 200) {
            // Success - return response body or success message
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.optString("message", "Draft sent successfully");
            } catch (JSONException e) {
                return "Draft sent successfully";
            }
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Failed to send draft with code: " + responseCode);
        } catch (JSONException e) {
            return "Failed to send draft. Server returned code: " + responseCode;
        }
    }
}
