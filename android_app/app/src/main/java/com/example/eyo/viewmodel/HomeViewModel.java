package com.example.eyo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.eyo.data.Mail;
import com.example.eyo.repository.UserRepository;
import com.example.eyo.utils.TokenManager;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    
    private MutableLiveData<String> currentFilter;
    private MutableLiveData<String> searchQuery;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<List<Mail>> mails;
    
    // Navigation states
    private MutableLiveData<String> selectedNavItem;
    
    // Repository
    private UserRepository userRepository;
    private TokenManager tokenManager;
    
    public HomeViewModel(@NonNull Application application) {
        super(application);
        initializeData();
    }
    
    private void initializeData() {
        currentFilter = new MutableLiveData<>();
        searchQuery = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        selectedNavItem = new MutableLiveData<>();
        mails = new MutableLiveData<>();
        
        // Initialize repository
        userRepository = new UserRepository();
        tokenManager = TokenManager.getInstance(getApplication());
        
        // Set default values
        currentFilter.setValue("inbox");
        searchQuery.setValue("");
        isLoading.setValue(false);
        selectedNavItem.setValue("inbox");
        mails.setValue(null);
    }
    
    // Getters for LiveData
    public LiveData<String> getCurrentFilter() {
        return currentFilter;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSelectedNavItem() {
        return selectedNavItem;
    }
    
    public LiveData<List<Mail>> getMails() {
        return mails;
    }
    
    // Methods to update state
    public void setCurrentFilter(String filter) {
        currentFilter.setValue(filter);
        selectedNavItem.setValue(filter);
    }
    
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }
    
    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
    
    public void setErrorMessage(String error) {
        errorMessage.setValue(error);
    }
    
    // Navigation methods
    public void navigateToInbox() {
        setCurrentFilter("inbox");
    }
    
    public void navigateToSent() {
        setCurrentFilter("sent");
    }
    
    public void navigateToDrafts() {
        setCurrentFilter("drafts");
    }
    
    public void navigateToStarred() {
        setCurrentFilter("starred");
    }
    
    public void navigateToSpam() {
        setCurrentFilter("spam");
    }
    
    // Label navigation methods
    public void navigateToExample() {
        setCurrentFilter("example");
    }
    
    // Compose action
    public void openCompose() {
        // TODO: Implement compose functionality
        // For now, just set a message
        setErrorMessage("Compose functionality will be implemented later");
    }
    
    // Search functionality
    public void performSearch(String query) {
        setSearchQuery(query);
        setLoading(true);
        setErrorMessage(null);
        
        if (query.trim().isEmpty()) {
            // If query is empty, clear results
            mails.setValue(null);
            setLoading(false);
            return;
        }
        
        // Build search query with current filter
        String searchQuery = buildSearchQuery(query);
        
        // Get the actual auth token from TokenManager
        String authToken = tokenManager.getBearerToken();
        
        if (authToken == null) {
            setErrorMessage("Authentication required. Please login again.");
            setLoading(false);
            return;
        }
        
        userRepository.searchMails(searchQuery, authToken, new UserRepository.SearchCallback() {
            @Override
            public void onSuccess(List<Mail> searchResults) {
                mails.setValue(searchResults);
                setLoading(false);
            }
            
            @Override
            public void onError(String error) {
                setErrorMessage("Search failed: " + error);
                setLoading(false);
            }
        });
    }
    
    private String buildSearchQuery(String query) {
        // Build search query based on current filter
        String currentFilterValue = currentFilter.getValue();
        if (currentFilterValue == null) {
            return query;
        }
        
        switch (currentFilterValue) {
            case "inbox":
                return query + " in:inbox";
            case "sent":
                return query + " in:sent";
            case "starred":
                return query + " in:starred";
            case "drafts":
                return query + " in:drafts";
            case "spam":
                return query + " in:spam";
            case "example":
                return query + " label:example";
            default:
                return query;
        }
    }
    
    // Get title for current filter
    public String getTitleForFilter(String filter) {
        switch (filter) {
            case "inbox": return "Inbox";
            case "sent": return "Sent";
            case "drafts": return "Drafts";
            case "starred": return "Starred";
            case "spam": return "Spam";
            case "example": return "EXAMPLE";
            default: return "EYO Mail";
        }
    }
    
    // Logout functionality
    public void logout() {
        tokenManager.clearToken();
        // Clear current data
        mails.setValue(null);
        setSearchQuery("");
        setErrorMessage(null);
        setLoading(false);
    }
} 