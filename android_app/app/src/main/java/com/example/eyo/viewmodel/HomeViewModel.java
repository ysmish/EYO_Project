package com.example.eyo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

public class HomeViewModel extends AndroidViewModel {
    
    private MutableLiveData<String> currentFilter;
    private MutableLiveData<String> searchQuery;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    
    // Navigation states
    private MutableLiveData<String> selectedNavItem;
    
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
        
        // Set default values
        currentFilter.setValue("inbox");
        searchQuery.setValue("");
        isLoading.setValue(false);
        selectedNavItem.setValue("inbox");
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
    
    public void navigateToTrash() {
        setCurrentFilter("trash");
    }
    
    // Label navigation methods
    public void navigateToImportant() {
        setCurrentFilter("important");
    }
    
    public void navigateToPersonal() {
        setCurrentFilter("personal");
    }
    
    public void navigateToWork() {
        setCurrentFilter("work");
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
        
        // TODO: Implement actual search functionality
        // For now, just simulate loading
        new android.os.Handler().postDelayed(() -> {
            setLoading(false);
            if (query.trim().isEmpty()) {
                setErrorMessage(null);
            } else {
                setErrorMessage("Search results for: " + query);
            }
        }, 1000);
    }
    
    // Get title for current filter
    public String getTitleForFilter(String filter) {
        switch (filter) {
            case "inbox": return "Inbox";
            case "sent": return "Sent";
            case "drafts": return "Drafts";
            case "starred": return "Starred";
            case "spam": return "Spam";
            case "trash": return "Trash";
            case "important": return "Important";
            case "personal": return "Personal";
            case "work": return "Work";
            default: return "EYO Mail";
        }
    }
} 