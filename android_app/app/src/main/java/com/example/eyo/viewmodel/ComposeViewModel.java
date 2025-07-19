package com.example.eyo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.eyo.data.ApiService;
import com.example.eyo.data.Mail;
import com.example.eyo.repository.UserRepository;
import com.example.eyo.utils.TokenManager;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class ComposeViewModel extends AndroidViewModel {
    
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<String> successMessage;
    
    // Repository
    private UserRepository userRepository;
    private TokenManager tokenManager;
    
    public ComposeViewModel(@NonNull Application application) {
        super(application);
        initializeData();
    }
    
    private void initializeData() {
        isLoading = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        successMessage = new MutableLiveData<>();
        
        // Initialize repository
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
    
    // Send email method
    public void sendEmail(List<String> toList, List<String> ccList, String subject, String body) {
        // Validate input
        if (toList == null || toList.isEmpty()) {
            errorMessage.setValue("Please enter at least one recipient");
            return;
        }
        
        if (subject == null || subject.trim().isEmpty()) {
            errorMessage.setValue("Please enter a subject");
            return;
        }
        
        if (body == null || body.trim().isEmpty()) {
            errorMessage.setValue("Please enter a message");
            return;
        }
        
        // Validate users exist in system
        validateUsersExist(toList, ccList, subject, body);
    }
    
    // Validate users exist in system
    private void validateUsersExist(List<String> toList, List<String> ccList, String subject, String body) {
        // Set loading state
        isLoading.setValue(true);
        errorMessage.setValue(null);
        successMessage.setValue(null);
        
        // Get auth token
        String authToken = tokenManager.getBearerToken();
        
        if (authToken == null) {
            errorMessage.setValue("Authentication required. Please login again.");
            isLoading.setValue(false);
            return;
        }
        
        // Collect all users to validate
        List<String> allUsers = new ArrayList<>();
        allUsers.addAll(toList);
        if (ccList != null) {
            allUsers.addAll(ccList);
        }
        
        // Remove duplicates and empty strings
        allUsers = new ArrayList<>(new HashSet<>(allUsers));
        allUsers.removeIf(user -> user == null || user.trim().isEmpty());
        
        if (allUsers.isEmpty()) {
            errorMessage.setValue("No valid users specified");
            isLoading.setValue(false);
            return;
        }
        
        // Validate all users asynchronously
        validateUsersBatch(allUsers, authToken, toList, ccList, subject, body, 0);
    }
    
    private void validateUsersBatch(List<String> users, String authToken, List<String> toList, List<String> ccList, String subject, String body, int index) {
        if (index >= users.size()) {
            // All users are valid, proceed with sending
            proceedWithSending(toList, ccList, subject, body, authToken);
            return;
        }
        
        String currentUser = users.get(index).trim();
        ApiService.checkUser(currentUser, authToken, new ApiService.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean userExists) {
                if (userExists) {
                    // User exists, check next user
                    validateUsersBatch(users, authToken, toList, ccList, subject, body, index + 1);
                } else {
                    // User doesn't exist
                    errorMessage.setValue("User '" + currentUser + "' not found in system");
                    isLoading.setValue(false);
                }
            }
            
            @Override
            public void onError(String error) {
                errorMessage.setValue("Error validating user '" + currentUser + "': " + error);
                isLoading.setValue(false);
            }
        });
    }
    
    private void proceedWithSending(List<String> toList, List<String> ccList, String subject, String body, String authToken) {
        // Create Mail object
        Mail mail = new Mail();
        mail.setTo(toList);
        mail.setCc(ccList);
        mail.setSubject(subject);
        mail.setBody(body);
        mail.setDate(new Date());
        mail.setRead(false);
        mail.setLabels(new ArrayList<>());
        
        // Send email through repository
        userRepository.sendMail(mail, authToken, new UserRepository.SendMailCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                successMessage.setValue("Email sent successfully!");
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to send email: " + error);
            }
        });
    }
    
    // Save as draft method
    public void saveAsDraft(List<String> toList, List<String> ccList, String subject, String body) {
        isLoading.setValue(true);
        
        // Get auth token
        String authToken = tokenManager.getBearerToken();
        
        if (authToken == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Authentication required. Please login again.");
            return;
        }

        // Clean the lists - remove empty entries
        List<String> cleanToList = null;
        if (toList != null) {
            cleanToList = new ArrayList<>();
            for (String user : toList) {
                if (user != null && !user.trim().isEmpty()) {
                    cleanToList.add(user.trim());
                }
            }
            if (cleanToList.isEmpty()) {
                cleanToList = null;
            }
        }

        List<String> cleanCcList = null;
        if (ccList != null) {
            cleanCcList = new ArrayList<>();
            for (String user : ccList) {
                if (user != null && !user.trim().isEmpty()) {
                    cleanCcList.add(user.trim());
                }
            }
            if (cleanCcList.isEmpty()) {
                cleanCcList = null;
            }
        }

        // Save draft through repository
        userRepository.saveDraft(cleanToList, cleanCcList, subject, body, authToken, new UserRepository.SaveDraftCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.setValue(false);
                // Only show message if there was actual content saved
                if (!"No content to save".equals(message)) {
                    successMessage.setValue("Draft saved");
                } else {
                    successMessage.setValue("No content to save");
                }
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to save draft: " + error);
            }
        });
    }
    
    // Clear messages
    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
} 