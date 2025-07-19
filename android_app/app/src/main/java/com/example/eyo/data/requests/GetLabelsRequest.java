package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import com.example.eyo.data.Label;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLabelsRequest implements ApiRequest<List<Label>> {
    private String authToken;

    public GetLabelsRequest(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String getEndpoint() {
        return "/labels";
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public String getRequestBody() {
        // GET request, no body needed
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
    public List<Label> parseResponse(int responseCode, String responseBody) {
        try {
            JSONArray jsonArray = new JSONArray(responseBody);
            List<Label> labels = new ArrayList<>();
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject labelJson = jsonArray.getJSONObject(i);
                
                Label label = new Label();
                label.setId(labelJson.getInt("id"));
                label.setName(labelJson.getString("name"));
                label.setColor(labelJson.optString("color", "#4F46E5")); // Default color if not provided
                
                labels.add(label);
            }
            
            return labels;
            
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse labels response: " + e.getMessage());
        }
    }

    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        try {
            JSONObject errorJson = new JSONObject(responseBody);
            return errorJson.optString("error", "Failed to fetch labels with code: " + responseCode);
        } catch (JSONException e) {
            return "Failed to fetch labels with code: " + responseCode;
        }
    }
} 