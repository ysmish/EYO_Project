package com.example.eyo.repository;

import com.example.eyo.data.ApiService;
import com.example.eyo.data.User;

public class UserRepository {
    
    public interface RegistrationCallback {
        void onSuccess(String message);
        void onError(String error);
    }
    
    public interface LoginCallback {
        void onSuccess(boolean userExists);
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
        ApiService.loginUser(username, password, new ApiService.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean userExists) {
                callback.onSuccess(userExists);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
} 