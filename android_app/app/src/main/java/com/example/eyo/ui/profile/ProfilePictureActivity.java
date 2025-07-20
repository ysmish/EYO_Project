package com.example.eyo.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.eyo.R;
import com.example.eyo.data.User;
import com.example.eyo.utils.TokenManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfilePictureActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 2001;
    private static final int PERMISSION_REQUEST_STORAGE = 2002;

    private ImageView ivProfilePicture;
    private Button btnChangeProfilePicture;
    private ImageButton btnClose;
    private User currentUser;
    private String currentPhotoPath;
    
    // ActivityResultLaunchers for camera and gallery
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        initViews();
        setupActivityResultLaunchers();
        setupClickListeners();
        loadUserData();
        

    }

    private void initViews() {
        ivProfilePicture = findViewById(R.id.iv_profile_picture_large);
        btnChangeProfilePicture = findViewById(R.id.btn_change_profile_picture);
        btnClose = findViewById(R.id.btn_close);
    }
    
    private void setupActivityResultLaunchers() {
        // Camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    handleCameraResult();
                } else {
                    Log.d("ProfilePicture", "Camera cancelled or failed");
                }
            }
        );
        
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGalleryResult(result.getData());
                } else {
                    Log.d("ProfilePicture", "Gallery cancelled or failed");
                }
            }
        );
        
        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d("ProfilePicture", "Camera permission granted");
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
                }
            }
        );
        
        // Storage permission launcher
        storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Log.d("ProfilePicture", "Storage permission granted");
                    openGallery();
                } else {
                    Toast.makeText(this, "Storage permission is required to access photos", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> finish());
        
        btnChangeProfilePicture.setOnClickListener(v -> showImageSourceDialog());
    }

    private void loadUserData() {
        // Get current user from TokenManager
        TokenManager tokenManager = TokenManager.getInstance(this);
        String username = tokenManager.getUsername();
        String authToken = tokenManager.getBearerToken();
        
        if (username != null && authToken != null) {
            // Load user data from API
            com.example.eyo.data.ApiService.getUserData(username, authToken, new com.example.eyo.data.ApiService.ApiCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        currentUser = user;
                        loadProfilePicture();
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Log.e("ProfilePicture", "Failed to load user data: " + error);
                        // Show default image on error
                        ivProfilePicture.setImageResource(R.drawable.ic_person);
                    });
                }
            });
        } else {
            // No user data available, show default
            ivProfilePicture.setImageResource(R.drawable.ic_person);
        }
    }

    private void loadProfilePicture() {
        if (currentUser != null && currentUser.getPhoto() != null && !currentUser.getPhoto().isEmpty()) {
            // Load the user's actual profile picture
            Glide.with(this)
                    .load(currentUser.getPhoto())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivProfilePicture);
        } else {
            // Show default image if no photo available
            ivProfilePicture.setImageResource(R.drawable.ic_person);
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery", "Cancel"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(options, (dialog, which) -> {
            Log.d("ProfilePicture", "Selected option: " + which);
            switch (which) {
                case 0: // Camera
                    Log.d("ProfilePicture", "Camera selected");
                    checkCameraPermissionAndOpen();
                    break;
                case 1: // Gallery
                    Log.d("ProfilePicture", "Gallery selected");
                    checkStoragePermissionAndOpen();
                    break;
                case 2: // Cancel
                    Log.d("ProfilePicture", "Cancel selected");
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    private void checkStoragePermissionAndOpen() {
        // For Android 13+ (API 33+), we don't need READ_EXTERNAL_STORAGE for gallery access
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            openGallery();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            openGallery();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        try {
            File photoFile = createImageFile();
            Uri photoURI = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }





    private void uploadProfilePicture(File imageFile) {
        Log.d("ProfilePicture", "Uploading profile picture from file: " + imageFile.getAbsolutePath());
        
        // Get auth token
        TokenManager tokenManager = TokenManager.getInstance(this);
        String authToken = tokenManager.getBearerToken();
        
        if (authToken == null) {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading state
        btnChangeProfilePicture.setEnabled(false);
        btnChangeProfilePicture.setText("Uploading...");
        
        // Upload to server
        com.example.eyo.data.ApiService.updateUserPhoto(authToken, imageFile, new com.example.eyo.data.ApiService.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfilePictureActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                    Log.d("ProfilePicture", "Upload successful: " + result);
                    
                    // Reset button state
                    btnChangeProfilePicture.setEnabled(true);
                    btnChangeProfilePicture.setText("Change Profile Picture");
                    
                    // Reload user data to show the updated profile picture
                    loadUserData();
                    
                    // Set result to indicate success
                    setResult(RESULT_OK);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfilePictureActivity.this, "Failed to upload: " + error, Toast.LENGTH_SHORT).show();
                    Log.e("ProfilePicture", "Upload failed: " + error);
                    
                    // Reset button state
                    btnChangeProfilePicture.setEnabled(true);
                    btnChangeProfilePicture.setText("Change Profile Picture");
                });
            }
        });
    }

    private void uploadProfilePictureFromUri(Uri imageUri) {
        Log.d("ProfilePicture", "Uploading profile picture from URI: " + imageUri.toString());
        
        // Get auth token
        TokenManager tokenManager = TokenManager.getInstance(this);
        String authToken = tokenManager.getBearerToken();
        
        if (authToken == null) {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading state
        btnChangeProfilePicture.setEnabled(false);
        btnChangeProfilePicture.setText("Uploading...");
        
        // Convert URI to file and upload
        try {
            // Create a temporary file from the URI
            File tempFile = createTempFileFromUri(imageUri);
            if (tempFile != null) {
                // Upload the temporary file
                uploadProfilePicture(tempFile);
            } else {
                Toast.makeText(this, "Failed to process selected image", Toast.LENGTH_SHORT).show();
                btnChangeProfilePicture.setEnabled(true);
                btnChangeProfilePicture.setText("Change Profile Picture");
            }
        } catch (Exception e) {
            Log.e("ProfilePicture", "Error processing URI", e);
            Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnChangeProfilePicture.setEnabled(true);
            btnChangeProfilePicture.setText("Change Profile Picture");
        }
    }
    
    private File createTempFileFromUri(Uri uri) throws IOException {
        // Create a temporary file
        File tempFile = File.createTempFile("profile_photo_", ".jpg", getCacheDir());
        
        // Copy the content from URI to the temporary file
        try (java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
             java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile)) {
            
            if (inputStream == null) {
                throw new IOException("Could not open input stream for URI");
            }
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        
        return tempFile;
    }
    
    private void handleCameraResult() {
        if (currentPhotoPath != null) {
            File photoFile = new File(currentPhotoPath);
            if (photoFile.exists()) {
                Glide.with(this)
                        .load(photoFile)
                        .circleCrop()
                        .into(ivProfilePicture);
                uploadProfilePicture(photoFile);
            } else {
                Toast.makeText(this, "Failed to capture photo", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to capture photo", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void handleGalleryResult(Intent data) {
        if (data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .into(ivProfilePicture);
            uploadProfilePictureFromUri(selectedImageUri);
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    

} 