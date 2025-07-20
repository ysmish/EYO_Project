package com.example.eyo.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.example.eyo.data.Label;
import com.example.eyo.data.Mail;
import com.example.eyo.data.User;
import com.example.eyo.repository.UserRepository;
import com.example.eyo.utils.TokenManager;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    
    private MutableLiveData<String> currentFilter;
    private MutableLiveData<String> searchQuery;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<List<Mail>> mails;
    private MutableLiveData<String> categoryTitle;
    private MutableLiveData<List<Label>> userLabels;
    private MutableLiveData<User> currentUser;
    
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
        categoryTitle = new MutableLiveData<>();
        userLabels = new MutableLiveData<>();
        currentUser = new MutableLiveData<>();
        
        // Initialize repository
        userRepository = new UserRepository();
        tokenManager = TokenManager.getInstance(getApplication());
        
        // Set default values
        currentFilter.setValue("inbox");
        searchQuery.setValue("");
        isLoading.setValue(false);
        selectedNavItem.setValue("inbox");
        mails.setValue(null);
        categoryTitle.setValue("Inbox mails");
        
        // Load user labels and current user data
        loadUserLabels();
        loadCurrentUserData();
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
    
    public LiveData<String> getCategoryTitle() {
        return categoryTitle;
    }
    
    public LiveData<List<Label>> getUserLabels() {
        return userLabels;
    }
    
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    
    // Methods to update state
    public void setCurrentFilter(String filter) {
        currentFilter.setValue(filter);
        selectedNavItem.setValue(filter);
        categoryTitle.setValue(getCategoryTitleText(filter));
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
        clearSearchAndNavigate("inbox");
    }
    
    public void navigateToSent() {
        clearSearchAndNavigate("sent");
    }
    
    public void navigateToDrafts() {
        clearSearchAndNavigate("drafts");
    }
    
    public void navigateToStarred() {
        clearSearchAndNavigate("starred");
    }
    
    public void navigateToSpam() {
        clearSearchAndNavigate("spam");
    }
    
    // Label navigation methods
    public void navigateToExample() {
        clearSearchAndNavigate("example");
    }
    
    // All mails navigation
    public void navigateToAll() {
        clearSearchAndNavigate("all");
    }
    
    // Label navigation
    public void navigateToLabel(String labelName) {
        // Set current filter and category title
        setCurrentFilter("all"); // Set filter to "all" to allow searching across all mails
        categoryTitle.setValue(labelName); // Just label name, no "mails" suffix
        
        // Perform the search without updating the search query field
        performLabelSearch(labelName);
    }
    
    // Private method to search by label without affecting the search bar
    private void performLabelSearch(String labelName) {
        setLoading(true);
        setErrorMessage(null);
        
        // Get the actual auth token from TokenManager
        String authToken = tokenManager.getBearerToken();
        
        if (authToken == null) {
            setErrorMessage("Authentication required. Please login again.");
            setLoading(false);
            return;
        }
        
        // Build search query for the label
        String searchQuery = "label:" + labelName;
        
        userRepository.searchMails(searchQuery, authToken, new UserRepository.SearchCallback() {
            @Override
            public void onSuccess(List<Mail> searchResults) {
                mails.setValue(searchResults);
                setLoading(false);
                // Keep search query empty to not show it in the search bar
                setSearchQuery("");
            }
            
            @Override
            public void onError(String error) {
                setErrorMessage("Failed to load " + labelName + " mails: " + error);
                setLoading(false);
                // Keep search query empty even on error
                setSearchQuery("");
            }
        });
    }
    
    // Load user labels
    public void loadUserLabels() {
        String authToken = tokenManager.getBearerToken();
        if (authToken == null) {
            setErrorMessage("Authentication required. Please login again.");
            return;
        }
        
        userRepository.getLabels(authToken, new UserRepository.GetLabelsCallback() {
            @Override
            public void onSuccess(List<Label> labels) {
                userLabels.setValue(labels);
            }
            
            @Override
            public void onError(String error) {
                setErrorMessage("Failed to load labels: " + error);
            }
        });
    }
    
    // Load current user data
    public void loadCurrentUserData() {
        String authToken = tokenManager.getBearerToken();
        String username = tokenManager.getUsername();
        
        if (authToken == null || username == null) {
            setErrorMessage("Authentication required. Please login again.");
            return;
        }
        
        userRepository.getUserData(username, authToken, new UserRepository.GetUserDataCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.setValue(user);
            }
            
            @Override
            public void onError(String error) {
                // Don't show error for user data loading failure as it's not critical
                // Just log it or set a default user
                currentUser.setValue(null);
            }
        });
    }
    
    // Helper method to clear search and navigate to category
    private void clearSearchAndNavigate(String category) {
        setCurrentFilter(category);
        setSearchQuery(""); // Clear search
        loadMailsForCategoryInternal(category);
    }
    
    // Public method to refresh current category mails
    public void refreshCurrentCategory() {
        String currentCategory = currentFilter.getValue();
        if (currentCategory != null) {
            loadMailsForCategoryInternal(currentCategory);
        }
    }
    
    // Load mails for specific category
    private void loadMailsForCategoryInternal(String category) {
        setLoading(true);
        setErrorMessage(null);
        
        // Get the actual auth token from TokenManager
        String authToken = tokenManager.getBearerToken();
        
        if (authToken == null) {
            setErrorMessage("Authentication required. Please login again.");
            setLoading(false);
            return;
        }
        
        // Build search query for the category (show all mails in that category)
        String searchQuery;
        if ("all".equals(category)) {
            // For "all" category, use "in:all" to get all mails without filtering
            searchQuery = "in:all";
        } else {
            searchQuery = "in:" + category;
        }
        
        userRepository.searchMails(searchQuery, authToken, new UserRepository.SearchCallback() {
            @Override
            public void onSuccess(List<Mail> searchResults) {
                mails.setValue(searchResults);
                setLoading(false);
            }
            
            @Override
            public void onError(String error) {
                setErrorMessage("Failed to load " + category + " mails: " + error);
                setLoading(false);
            }
        });
    }
    
    // Search functionality
    public void performSearch(String query) {
        setSearchQuery(query);
        setLoading(true);
        setErrorMessage(null);
        
        if (query.trim().isEmpty()) {
            // If query is empty, reload current category mails
            String currentCategory = currentFilter.getValue();
            if (currentCategory != null) {
                loadMailsForCategoryInternal(currentCategory);
            } else {
                mails.setValue(null);
                setLoading(false);
            }
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
        if (currentFilterValue == null || "all".equals(currentFilterValue)) {
            return query; // No filter for "all" category
        }
        
        switch (currentFilterValue) {
            case "inbox":
                return "in:inbox " + query;
            case "sent":
                return "in:sent " + query;
            case "starred":
                return "in:starred " + query;
            case "drafts":
                return "in:drafts " + query;
            case "spam":
                return "in:spam " + query;
            default:
                return query;
        }
    }
    
    // Get title for current filter (for toolbar/header)
    public String getTitleForFilter(String filter) {
        switch (filter) {
            case "inbox": return "Inbox";
            case "sent": return "Sent";
            case "drafts": return "Drafts";
            case "starred": return "Starred";
            case "spam": return "Spam";
            case "example": return "EXAMPLE";
            case "all": return "All";
            default: return "EYO Mail";
        }
    }
    
    // Get category title with "mails" suffix for display
    private String getCategoryTitleText(String filter) {
        switch (filter) {
            case "inbox": return "Inbox mails";
            case "sent": return "Sent mails";
            case "drafts": return "Draft mails";
            case "starred": return "Starred mails";
            case "spam": return "Spam mails";
            case "example": return "EXAMPLE mails";
            case "all": return "All mails";
            default: return "All mails";
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