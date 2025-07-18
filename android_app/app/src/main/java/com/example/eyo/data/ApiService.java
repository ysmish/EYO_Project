package com.example.eyo.data;

import com.example.eyo.data.requests.LoginRequest;
import com.example.eyo.data.requests.RegisterRequest;
import com.example.eyo.data.requests.SearchRequest;

import java.util.List;

public class ApiService {
    
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    public static void registerUser(User user, ApiCallback<String> callback) {
        RegisterRequest request = new RegisterRequest(user);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void loginUser(String username, String password, ApiCallback<String> callback) {
        LoginRequest request = new LoginRequest(username, password);
        new GenericApiTask<>(request, callback).execute();
    }
    
    public static void searchMails(String query, String authToken, ApiCallback<List<Mail>> callback) {
        SearchRequest request = new SearchRequest(query, authToken);
        new GenericApiTask<>(request, callback).execute();
    }
} 