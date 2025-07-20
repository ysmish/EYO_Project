package com.example.eyo.data.requests;

import com.example.eyo.data.ApiRequest;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdatePhotoRequest implements ApiRequest<String> {
    
    private final String authToken;
    private final File imageFile;
    private final String imageUri;
    
    public UpdatePhotoRequest(String authToken, File imageFile) {
        this.authToken = authToken;
        this.imageFile = imageFile;
        this.imageUri = null;
    }
    
    public UpdatePhotoRequest(String authToken, String imageUri) {
        this.authToken = authToken;
        this.imageFile = null;
        this.imageUri = imageUri;
    }
    
    @Override
    public String getEndpoint() {
        return "/users/photo";
    }
    
    @Override
    public String getMethod() {
        return "POST";
    }
    
    @Override
    public String getRequestBody() {
        // This will be handled as multipart form data
        return null;
    }
    
    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (authToken != null && !authToken.isEmpty()) {
            headers.put("Authorization", authToken);
        }
        headers.put("Content-Type", "multipart/form-data");
        return headers;
    }
    
    @Override
    public String parseResponse(int responseCode, String responseBody) {
        if (responseCode == 200) {
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.optString("message", "Photo updated successfully");
            } catch (Exception e) {
                return "Photo updated successfully";
            }
        }
        return null;
    }
    
    @Override
    public String getErrorMessage(int responseCode, String responseBody) {
        if (responseCode >= 200 && responseCode < 300) {
            return null; // No error for success responses
        }
        
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.optString("error", "Upload failed with response code: " + responseCode);
        } catch (Exception e) {
            return "Upload failed with response code: " + responseCode;
        }
    }
    
    public File getImageFile() {
        return imageFile;
    }
    
    public String getImageUri() {
        return imageUri;
    }
    
    public boolean isFileUpload() {
        return imageFile != null;
    }
    
    public boolean isUriUpload() {
        return imageUri != null;
    }
} 