package com.example.eyo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.eyo.data.User;
import com.example.eyo.repository.UserRepository;

public class RegistrationViewModel extends AndroidViewModel {
    
    private UserRepository userRepository;
    private MutableLiveData<String> registrationResult;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;
    
    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        registrationResult = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
    }
    
    public LiveData<String> getRegistrationResult() {
        return registrationResult;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void registerUser(String firstName, String lastName, String birthday, 
                           String username, String password) {
        
        // Basic validation
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() || 
            birthday.trim().isEmpty() || username.trim().isEmpty() || 
            password.trim().isEmpty()) {
            errorMessage.setValue("All fields are required");
            return;
        }
        
        if (password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters long");
            return;
        }
        
        if (username.length() < 3) {
            errorMessage.setValue("Username must be at least 3 characters long");
            return;
        }
        
        // Check if username contains only alphanumeric characters
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            errorMessage.setValue("Username can only contain alphanumeric characters");
            return;
        }
        
        // Check if first name and last name contain only alphanumeric characters
        if (!firstName.matches("^[a-zA-Z0-9]+$") || !lastName.matches("^[a-zA-Z0-9]+$")) {
            errorMessage.setValue("First name and last name can only contain alphanumeric characters");
            return;
        }
        
        // Check birthday format (YYYY-MM-DD)
        if (!birthday.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            errorMessage.setValue("Birthday must be in YYYY-MM-DD format");
            return;
        }
        
        isLoading.setValue(true);
        
        User user = new User(firstName, lastName, birthday, username, password);
        
        userRepository.registerUser(user, new UserRepository.RegistrationCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.setValue(false);
                registrationResult.setValue(message);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
} 