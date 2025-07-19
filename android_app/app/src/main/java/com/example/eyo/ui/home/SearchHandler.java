package com.example.eyo.ui.home;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.eyo.viewmodel.HomeViewModel;

public class SearchHandler {
    
    private final HomeViewModel viewModel;
    private final EditText searchEditText;
    
    public SearchHandler(HomeViewModel viewModel, EditText searchEditText) {
        this.viewModel = viewModel;
        this.searchEditText = searchEditText;
        setupSearchFunctionality();
    }
    
    /**
     * Sets up search functionality including editor action listener and text change listener
     */
    private void setupSearchFunctionality() {
        // Search functionality - on editor action (Enter key)
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            viewModel.performSearch(query);
            return true;
        });
        
        // Add text change listener for real-time search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                viewModel.performSearch(query);
            }
        });
    }
    
    /**
     * Updates the search query in the EditText if needed (e.g., when cleared by navigation)
     * @param query The query string to set
     */
    public void updateSearchQuery(String query) {
        if (query != null && !searchEditText.getText().toString().equals(query)) {
            searchEditText.setText(query);
            // Move cursor to end if setting text
            searchEditText.setSelection(query.length());
        }
    }
} 