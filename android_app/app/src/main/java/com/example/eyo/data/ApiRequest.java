package com.example.eyo.data;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public interface ApiRequest<T> {
    String getEndpoint();
    String getMethod();
    String getRequestBody();
    T parseResponse(int responseCode, String responseBody);
    String getErrorMessage(int responseCode, String responseBody);
    
    // Optional method for adding custom headers
    default Map<String, String> getHeaders() {
        return null;
    }
} 