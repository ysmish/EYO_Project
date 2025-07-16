package com.example.eyo.ui.auth;

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

    private TextInputEditText etFirstName, etLastName, etBirthday, etUsername, etPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvMessage;
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
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
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

            viewModel.registerUser(firstName, lastName, birthday, username, password);
        });

        // Birthday field click listener for date picker
        etBirthday.setOnClickListener(v -> {
            DatePickerHelper datePickerHelper = new DatePickerHelper(this, formattedDate -> {
                etBirthday.setText(formattedDate);
            });
            
            datePickerHelper.setMaxDateToToday().showDatePicker();
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
    }
} 