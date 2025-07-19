package com.example.eyo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.eyo.repository.UserRepository;
import com.example.eyo.utils.TokenManager;

public class CreateLabelViewModel extends AndroidViewModel {
    
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<String> successMessage;
    
    // Repository and token manager
    private UserRepository userRepository;
    private TokenManager tokenManager;
    
    public CreateLabelViewModel(@NonNull Application application) {
        super(application);
        initializeData();
    }
    
    private void initializeData() {
        isLoading = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        successMessage = new MutableLiveData<>();
        
        // Initialize repository and token manager
        userRepository = new UserRepository();
        tokenManager = TokenManager.getInstance(getApplication());
        
        // Set default values
        isLoading.setValue(false);
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
    
    // Getters for LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    // Create label method
    public void createLabel(String labelName) {
        // Validate input
        if (labelName == null || labelName.trim().isEmpty()) {
            errorMessage.setValue("Please enter a label name");
            return;
        }
        
        // Get auth token
        String authToken = tokenManager.getBearerToken();
        if (authToken == null) {
            errorMessage.setValue("Authentication required. Please login again.");
            return;
        }
        
        // Start loading
        isLoading.setValue(true);
        errorMessage.setValue(null);
        successMessage.setValue(null);
        
        // Create label with default color
        String defaultColor = "#4F46E5";
        
        userRepository.createLabel(labelName.trim(), defaultColor, authToken, new UserRepository.CreateLabelCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.setValue(false);
                successMessage.setValue("Label \"" + labelName.trim() + "\" created successfully!");
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to create label: " + error);
            }
        });
    }
    
    // Clear messages method
    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
} 