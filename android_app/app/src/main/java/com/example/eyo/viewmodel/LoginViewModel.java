package com.example.eyo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.eyo.repository.UserRepository;

public class LoginViewModel extends AndroidViewModel {
    
    private UserRepository userRepository;
    private MutableLiveData<Boolean> loginResult;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;
    
    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        loginResult = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
    }
    
    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void loginUser(String username, String password) {
        
        // Basic validation
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            errorMessage.setValue("Username and password are required");
            return;
        }
        
        if (username.length() < 3) {
            errorMessage.setValue("Username must be at least 3 characters long");
            return;
        }
        
        if (password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters long");
            return;
        }
        
        isLoading.setValue(true);
        
        userRepository.loginUser(username, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(boolean userExists) {
                isLoading.setValue(false);
                loginResult.setValue(userExists);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
} 