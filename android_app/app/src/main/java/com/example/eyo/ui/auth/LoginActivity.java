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
import com.example.eyo.ui.home.HomeActivity;
import com.example.eyo.viewmodel.LoginViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvMessage, tvRegister;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            viewModel.loginUser(username, password);
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                btnLogin.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                tvMessage.setVisibility(View.GONE);
            } else {
                btnLogin.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getLoginResult().observe(this, userExists -> {
            if (userExists != null && userExists) {
                tvMessage.setText("Login successful!");
                tvMessage.setTextColor(Color.GREEN);
                tvMessage.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show();
                
                // Navigate to home activity
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Close login activity
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
} 