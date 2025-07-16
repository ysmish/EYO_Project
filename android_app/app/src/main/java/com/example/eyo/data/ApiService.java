package com.example.eyo.data;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ApiService {
    
    // Base URL for the web server - change this to your actual server IP
    private static final String BASE_URL = "http://10.0.2.2:3000/api";
    
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    public static void registerUser(User user, ApiCallback<String> callback) {
        new RegisterUserTask(callback).execute(user);
    }
    
    private static class RegisterUserTask extends AsyncTask<User, Void, ApiResponse<String>> {
        private ApiCallback<String> callback;
        
        public RegisterUserTask(ApiCallback<String> callback) {
            this.callback = callback;
        }
        
        @Override
        protected ApiResponse<String> doInBackground(User... users) {
            User user = users[0];
            try {
                URL url = new URL(BASE_URL + "/users");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                // Set request method and headers
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                
                // Create JSON payload
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("firstName", user.getFirstName());
                jsonPayload.put("lastName", user.getLastName());
                jsonPayload.put("birthday", user.getBirthday());
                jsonPayload.put("username", user.getUsername());
                jsonPayload.put("password", user.getPassword());
                if (user.getPhoto() != null) {
                    jsonPayload.put("photo", user.getPhoto());
                }
                
                // Send request
                OutputStream os = connection.getOutputStream();
                os.write(jsonPayload.toString().getBytes());
                os.flush();
                os.close();
                
                // Read response
                int responseCode = connection.getResponseCode();
                BufferedReader reader;
                
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
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
                
                // Parse response
                JSONObject jsonResponse = new JSONObject(response.toString());
                
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    return new ApiResponse<>(true, jsonResponse.optString("message"), "User registered successfully");
                } else {
                    String error = jsonResponse.optString("error");
                    if (error.isEmpty()) {
                        error = jsonResponse.optString("message");
                    }
                    return new ApiResponse<>(false, error);
                }
                
            } catch (Exception e) {
                return new ApiResponse<>(false, "Network error: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(ApiResponse<String> response) {
            if (response.isSuccess()) {
                callback.onSuccess(response.getMessage());
            } else {
                callback.onError(response.getError());
            }
        }
    }
} 