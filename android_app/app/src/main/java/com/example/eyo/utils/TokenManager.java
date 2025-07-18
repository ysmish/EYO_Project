package com.example.eyo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "EyoMailPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private static TokenManager instance;
    private SharedPreferences sharedPreferences;
    
    private TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Save the authentication token and user info
     */
    public void saveToken(String token, String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    /**
     * Get the stored authentication token
     */
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    
    /**
     * Get the stored username
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && getToken() != null;
    }
    
    /**
     * Get the token for API calls (without Bearer prefix since this server expects raw token)
     */
    public String getBearerToken() {
        return getToken();
    }
    
    /**
     * Clear all stored authentication data (logout)
     */
    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.apply();
    }
    
    /**
     * Clear all stored data
     */
    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
} 