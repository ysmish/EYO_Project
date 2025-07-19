package com.example.eyo.ui.labels;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eyo.R;
import com.example.eyo.viewmodel.CreateLabelViewModel;

public class CreateLabelActivity extends AppCompatActivity {

    private EditText etLabelName;
    private ImageButton btnCreate, btnClose;
    
    private CreateLabelViewModel viewModel;
    private boolean isWaitingForOperation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_label);

        initViews();
        setupViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etLabelName = findViewById(R.id.et_label_name);
        btnCreate = findViewById(R.id.btn_create);
        btnClose = findViewById(R.id.btn_close);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CreateLabelViewModel.class);
        // Clear any previous messages
        viewModel.clearMessages();
    }

    private void setupClickListeners() {
        // Create button
        btnCreate.setOnClickListener(v -> {
            createLabel();
        });

        // Close button
        btnClose.setOnClickListener(v -> {
            finish();
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                // Disable buttons during loading
                btnCreate.setEnabled(false);
                btnClose.setEnabled(false);
            } else {
                // Re-enable buttons when not loading
                btnCreate.setEnabled(true);
                btnClose.setEnabled(true);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                // Reset waiting flag on error
                isWaitingForOperation = false;
            }
        });

        viewModel.getSuccessMessage().observe(this, success -> {
            if (success != null) {
                Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
                
                // Close activity after successful label creation
                if (isWaitingForOperation) {
                    // Set result to indicate that a label was created successfully
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void createLabel() {
        String labelName = etLabelName.getText().toString().trim();

        // Validate label name
        if (labelName.isEmpty()) {
            etLabelName.setError("Please enter a label name");
            etLabelName.requestFocus();
            return;
        }

        // Clear any previous errors
        etLabelName.setError(null);

        // Create label through ViewModel and wait for completion
        isWaitingForOperation = true;
        viewModel.createLabel(labelName);
    }

    @Override
    public void onBackPressed() {
        // Just close the activity
        finish();
    }
} 