package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import com.example.eyo.data.Mail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendMailRequest implements ApiRequest<String> {
    
    private final Mail mail;
    private final String authToken;
    
    public SendMailRequest(Mail mail, String authToken) {
        this.mail = mail;
        this.authToken = authToken;
    }
    
    @Override
    public String getEndpoint() {
        return "/mails";
    }
    
    @Override
    public String getMethod() {
        return "POST";
    }
    
    @Override
    public String getRequestBody() {
        if (mail == null) {
            return "";
        }
        
        try {
            JSONObject jsonPayload = new JSONObject();
            
            // Add To field
            JSONArray toArray = new JSONArray();
            if (mail.getTo() != null) {
                for (String recipient : mail.getTo()) {
                    toArray.put(recipient);
                }
            }
            jsonPayload.put("to", toArray);
            
            // Add CC field
            JSONArray ccArray = new JSONArray();
            if (mail.getCc() != null) {
                for (String recipient : mail.getCc()) {
                    ccArray.put(recipient);
                }
            }
            jsonPayload.put("cc", ccArray);
            
            // Add Subject
            jsonPayload.put("subject", mail.getSubject() != null ? mail.getSubject() : "");
            
            // Add Body
            jsonPayload.put("body", mail.getBody() != null ? mail.getBody() : "");
            
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
                return jsonResponse.optString("message", "Email sent successfully");
            } catch (JSONException e) {
                return "Email sent successfully";
            }
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Failed to send email with code: " + responseCode);
        } catch (JSONException e) {
            return "Failed to send email with code: " + responseCode;
        }
    }
} 