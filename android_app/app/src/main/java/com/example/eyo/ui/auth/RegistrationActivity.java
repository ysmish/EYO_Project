package com.example.eyo.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eyo.R;
import com.example.eyo.utils.DatePickerHelper;
import com.example.eyo.viewmodel.RegistrationViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etBirthday, etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvMessage, tvLogin;
    private RegistrationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        setupViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etBirthday = findViewById(R.id.etBirthday);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String birthday = etBirthday.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Validate password confirmation
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.registerUser(firstName, lastName, birthday, username, password);
        });

        // Birthday field click listener for date picker
        etBirthday.setOnClickListener(v -> {
            DatePickerHelper datePickerHelper = new DatePickerHelper(this, formattedDate -> {
                etBirthday.setText(formattedDate);
            });
            
            datePickerHelper.setMaxDateToToday().showDatePicker();
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close registration activity
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                btnRegister.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                tvMessage.setVisibility(View.GONE);
            } else {
                btnRegister.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getRegistrationResult().observe(this, result -> {
            if (result != null) {
                tvMessage.setText(result);
                tvMessage.setTextColor(Color.GREEN);
                tvMessage.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
                
                // Clear form fields after successful registration
                clearForm();
                
                // Auto-redirect to login activity after a short delay
                new android.os.Handler().postDelayed(() -> {
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }, 2000); // 2 second delay to show the success message
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                tvMessage.setText(error);
                tvMessage.setTextColor(Color.RED);
                tvMessage.setVisibility(View.VISIBLE);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearForm() {
        etFirstName.setText("");
        etLastName.setText("");
        etBirthday.setText("");
        etUsername.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }
} 