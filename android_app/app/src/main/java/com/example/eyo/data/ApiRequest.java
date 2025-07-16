package com.example.eyo.data;

import java.net.HttpURLConnection;
import java.net.URL;

public interface ApiRequest<T> {
    String getEndpoint();
    String getMethod();
    String getRequestBody();
    T parseResponse(int responseCode, String responseBody);
    String getErrorMessage(int responseCode, String responseBody);
} 