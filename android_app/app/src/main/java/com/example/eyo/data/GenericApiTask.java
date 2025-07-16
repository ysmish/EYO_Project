package com.example.eyo.data;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
            callback.onError(response.getError());
        }
    }
} 