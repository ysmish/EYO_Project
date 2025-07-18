package com.example.eyo.repository;

import com.example.eyo.data.ApiService;
import com.example.eyo.data.Mail;
import com.example.eyo.data.User;

import java.util.List;

public class UserRepository {
    
    public interface RegistrationCallback {
        void onSuccess(String message);
        void onError(String error);
    }
    
    public interface LoginCallback {
        void onSuccess(String token);
        void onError(String error);
    }
    
    public interface SearchCallback {
        void onSuccess(List<Mail> mails);
        void onError(String error);
    }
    
    public void registerUser(User user, RegistrationCallback callback) {
        ApiService.registerUser(user, new ApiService.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void loginUser(String username, String password, LoginCallback callback) {
        ApiService.loginUser(username, password, new ApiService.ApiCallback<String>() {
            @Override
            public void onSuccess(String token) {
                callback.onSuccess(token);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public void searchMails(String query, String authToken, SearchCallback callback) {
        if (authToken == null || authToken.isEmpty()) {
            callback.onError("Authentication token is required");
            return;
        }
        
        ApiService.searchMails(query, authToken, new ApiService.ApiCallback<List<Mail>>() {
            @Override
            public void onSuccess(List<Mail> mails) {
                callback.onSuccess(mails);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
} 