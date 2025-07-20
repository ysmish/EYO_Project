package com.example.eyo.data;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GenericApiTask<T> extends AsyncTask<Void, Void, ApiResponse<T>> {
    
    private static final String BASE_URL = "http://10.0.2.2:3000/api";
    private ApiRequest<T> request;
    private ApiService.ApiCallback<T> callback;
    
    public GenericApiTask(ApiRequest<T> request, ApiService.ApiCallback<T> callback) {
        this.request = request;
        this.callback = callback;
    }
    
    @Override
    protected ApiResponse<T> doInBackground(Void... voids) {
        try {
            URL url = new URL(BASE_URL + request.getEndpoint());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set request method and headers
            connection.setRequestMethod(request.getMethod());
            connection.setRequestProperty("Content-Type", "application/json");
            
            // Add custom headers if provided
            Map<String, String> headers = request.getHeaders();
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
            // Send request body if not GET
            if (!request.getMethod().equals("GET")) {
                connection.setDoOutput(true);
                String requestBody = request.getRequestBody();
                if (!requestBody.isEmpty()) {
                    OutputStream os = connection.getOutputStream();
                    os.write(requestBody.getBytes());
                    os.flush();
                    os.close();
                }
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Parse response using the specific request handler
            if (responseCode >= 200 && responseCode < 300) {
                T data = request.parseResponse(responseCode, response.toString());
                return new ApiResponse<>(true, data, "Success");
            } else {
                // Handle 401 Unauthorized - session expired
                if (responseCode == 401) {
                    return new ApiResponse<>(false, "Session expired. Please login again.");
                }
                
                String error = request.getErrorMessage(responseCode, response.toString());
                return new ApiResponse<>(false, error);
            }
            
        } catch (Exception e) {
            return new ApiResponse<>(false, "Network error: " + e.getMessage());
        }
    }
    
    @Override
    protected void onPostExecute(ApiResponse<T> response) {
        if (response.isSuccess()) {
            callback.onSuccess(response.getData());
        } else {
            // If it's a session expired error, we need to handle it specially
            if (response.getError().contains("Session expired")) {
                // Clear the token and redirect to login
                clearTokenAndRedirectToLogin();
            }
            callback.onError(response.getError());
        }
    }
    
    private void clearTokenAndRedirectToLogin() {
        // Clear the stored token
        try {
            Class<?> tokenManagerClass = Class.forName("com.example.eyo.utils.TokenManager");
            java.lang.reflect.Method getInstanceMethod = tokenManagerClass.getMethod("getInstance", android.content.Context.class);
            Object tokenManager = getInstanceMethod.invoke(null, (Object) null);
            java.lang.reflect.Method clearTokenMethod = tokenManagerClass.getMethod("clearToken");
            clearTokenMethod.invoke(tokenManager);
        } catch (Exception e) {
            // If we can't clear the token, just log it
            android.util.Log.w("GenericApiTask", "Could not clear token on session expiry");
        }
    }
} 