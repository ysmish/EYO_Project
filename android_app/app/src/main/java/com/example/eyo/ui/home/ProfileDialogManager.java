package com.example.eyo.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eyo.R;
import com.example.eyo.data.User;

public class ProfileDialogManager {
    
    private final Context context;
    
    public ProfileDialogManager(Context context) {
        this.context = context;
    }
    
    /**
     * Shows the user profile dialog with user information
     * @param user The user object containing profile data
     */
    public void showUserProfileDialog(User user) {
        if (user == null) {
            Toast.makeText(context, "User data not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_user_profile, null);
        
        // Get views from dialog
        ImageView ivProfilePicture = dialogView.findViewById(R.id.iv_profile_picture);
        TextView tvUserName = dialogView.findViewById(R.id.tv_user_name);
        TextView tvUsername = dialogView.findViewById(R.id.tv_username);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        
        // Set user data
        String fullName = user.getFirstName() + " " + user.getLastName();
        tvUserName.setText(fullName);
        tvUsername.setText(user.getUsername() + "@eyo.com");
        
        // Load profile picture using Glide
        if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
            Glide.with(context)
                    .load(user.getPhoto())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_person);
        }
        
        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        btnOk.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * Updates the profile button with user's profile picture or default icon
     * @param user The user object containing profile data
     * @param profileButton The ImageButton to update
     */
    public void updateProfileButton(User user, android.widget.ImageButton profileButton) {
        if (user != null && user.getPhoto() != null && !user.getPhoto().isEmpty()) {
            // Debug logging
            Log.d("ProfileDialog", "Loading profile picture: " + user.getPhoto());
            
            // Load profile picture into the button using Glide
            Glide.with(context)
                    .load(user.getPhoto())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(profileButton);
            
            // Remove tint when showing actual photo
            profileButton.setImageTintList(null);
        } else {
            // Debug logging
            Log.d("ProfileDialog", "No profile picture found, using default. User: " + (user != null ? "exists" : "null") + 
                  ", Photo: " + (user != null ? user.getPhoto() : "N/A"));
            
            // Use default icon without tint to keep original gray color
            profileButton.setImageResource(R.drawable.ic_person);
            profileButton.setImageTintList(null);
        }
    }
} 