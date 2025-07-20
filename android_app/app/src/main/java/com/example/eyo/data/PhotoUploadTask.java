package com.example.eyo.data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.eyo.data.requests.UpdatePhotoRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoUploadTask {
    
    private static final String BASE_URL = "http://10.0.2.2:3000/api";
    
    private final UpdatePhotoRequest request;
    private final ApiService.ApiCallback<String> callback;
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public PhotoUploadTask(UpdatePhotoRequest request, ApiService.ApiCallback<String> callback) {
        this.request = request;
        this.callback = callback;
    }
    
    public void execute() {
        executor.execute(() -> {
            PhotoUploadResult result = doInBackground();
            if (result.isSuccess()) {
                callback.onSuccess(result.getMessage());
            } else {
                callback.onError(result.getError());
            }
        });
    }
    
    private PhotoUploadResult doInBackground() {
        try {
            String urlString = BASE_URL + request.getEndpoint();
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set up the connection
            connection.setRequestMethod(request.getMethod());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            
            // Set headers
            Map<String, String> headers = request.getHeaders();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (!"Content-Type".equals(header.getKey())) { // We'll set this for multipart
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
            // Create multipart boundary
            String boundary = "*****" + System.currentTimeMillis() + "*****";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            // Write multipart data
            try (OutputStream outputStream = connection.getOutputStream()) {
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                
                // Start content
                outputStream.write((twoHyphens + boundary + lineEnd).getBytes(StandardCharsets.UTF_8));
                outputStream.write(("Content-Disposition: form-data; name=\"photo\"; filename=\"photo.jpg\"" + lineEnd).getBytes(StandardCharsets.UTF_8));
                outputStream.write(("Content-Type: image/jpeg" + lineEnd).getBytes(StandardCharsets.UTF_8));
                outputStream.write(lineEnd.getBytes(StandardCharsets.UTF_8));
                
                // Write file data
                if (request.isFileUpload() && request.getImageFile() != null) {
                    File file = request.getImageFile();
                    byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
                    outputStream.write(fileBytes);
                } else if (request.isUriUpload() && request.getImageUri() != null) {
                    // For URI uploads, we'd need to read from the URI
                    // This is a simplified version - in practice you'd need to handle different URI schemes
                    Log.w("PhotoUploadTask", "URI upload not fully implemented");
                }
                
                // End content
                outputStream.write(lineEnd.getBytes(StandardCharsets.UTF_8));
                outputStream.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes(StandardCharsets.UTF_8));
            }
            
            // Get response
            int responseCode = connection.getResponseCode();
            InputStream inputStream = responseCode >= 200 && responseCode < 300 
                    ? connection.getInputStream() 
                    : connection.getErrorStream();
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            
            String responseBody = response.toString();
            String result = request.parseResponse(responseCode, responseBody);
            
            if (result != null) {
                return new PhotoUploadResult(true, result, null);
            } else {
                return new PhotoUploadResult(false, null, "Upload failed with response code: " + responseCode);
            }
            
        } catch (Exception e) {
            Log.e("PhotoUploadTask", "Error uploading photo", e);
            return new PhotoUploadResult(false, null, "Upload error: " + e.getMessage());
        }
    }
    
    public static class PhotoUploadResult {
        private final boolean success;
        private final String message;
        private final String error;
        
        public PhotoUploadResult(boolean success, String message, String error) {
            this.success = success;
            this.message = message;
            this.error = error;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getError() {
            return error;
        }
    }
} 