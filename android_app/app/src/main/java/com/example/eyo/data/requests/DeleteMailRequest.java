package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeleteMailRequest implements ApiRequest<String> {
    
    private final int mailId;
    private final String authToken;
    
    public DeleteMailRequest(int mailId, String authToken) {
        this.mailId = mailId;
        this.authToken = authToken;
    }
    
    @Override
    public String getEndpoint() {
        return "/mails/" + mailId;
    }
    
    @Override
    public String getMethod() {
        return "DELETE";
    }
    
    @Override
    public String getRequestBody() {
        // DELETE request, no body needed
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
    public String parseResponse(int responseCode, String responseBody) {
        if (responseCode == 204 || responseCode == 200) {
            return "Mail deleted successfully";
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Failed to delete mail with code: " + responseCode);
        } catch (JSONException e) {
            return "Failed to delete mail with code: " + responseCode;
        }
    }
}
