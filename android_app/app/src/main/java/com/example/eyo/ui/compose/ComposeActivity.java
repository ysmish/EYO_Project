package com.example.eyo.ui.compose;

import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eyo.R;
import com.example.eyo.viewmodel.ComposeViewModel;

import java.util.Arrays;
import java.util.List;

public class ComposeActivity extends AppCompatActivity {

    private EditText etTo, etCc, etSubject, etBody;
    private ImageButton btnAddCc, btnRemoveCc;
    private ImageButton btnSend, btnCloseDraft, btnDelete;
    private LinearLayout layoutCc;
    private View dividerCc;
    
    private ComposeViewModel viewModel;
    private boolean isCcVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        initViews();
        setupViewModel();
        setupClickListeners();
        setupCcFieldWatcher();
        observeViewModel();
    }

    private void initViews() {
        etTo = findViewById(R.id.et_to);
        etCc = findViewById(R.id.et_cc);
        etSubject = findViewById(R.id.et_subject);
        etBody = findViewById(R.id.et_body);
        btnAddCc = findViewById(R.id.btn_add_cc);
        btnRemoveCc = findViewById(R.id.btn_remove_cc);
        btnSend = findViewById(R.id.btn_send);
        btnCloseDraft = findViewById(R.id.btn_close_draft);
        btnDelete = findViewById(R.id.btn_delete);
        layoutCc = findViewById(R.id.layout_cc);
        dividerCc = findViewById(R.id.divider_cc);
    }



    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ComposeViewModel.class);
    }

    private void setupClickListeners() {
        // Add/Remove CC functionality
        btnAddCc.setOnClickListener(v -> {
            toggleCcVisibility(true);
        });

        btnRemoveCc.setOnClickListener(v -> {
            toggleCcVisibility(false);
            etCc.setText(""); // Clear CC field when hiding
        });

        // Send button
        btnSend.setOnClickListener(v -> {
            sendEmail();
        });

        // Close and save as draft button
        btnCloseDraft.setOnClickListener(v -> {
            saveAsDraftAndClose();
        });

        // Delete without saving button
        btnDelete.setOnClickListener(v -> {
            deleteWithoutSaving();
        });
    }

    private void setupCcFieldWatcher() {
        etCc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Hide CC button if there's content in CC field
                if (s.toString().trim().isEmpty()) {
                    btnRemoveCc.setVisibility(View.VISIBLE);
                } else {
                    btnRemoveCc.setVisibility(View.GONE);
                }
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                btnSend.setEnabled(false);
            } else {
                btnSend.setEnabled(true);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, success -> {
            if (success != null) {
                Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
                finish(); // Close activity after successful send
            }
        });
    }

    private void toggleCcVisibility(boolean show) {
        if (show) {
            dividerCc.setVisibility(View.VISIBLE);
            layoutCc.setVisibility(View.VISIBLE);
            btnAddCc.setVisibility(View.GONE);
            isCcVisible = true;
        } else {
            dividerCc.setVisibility(View.GONE);
            layoutCc.setVisibility(View.GONE);
            btnAddCc.setVisibility(View.VISIBLE);
            isCcVisible = false;
        }
    }

    private void sendEmail() {
        String to = etTo.getText().toString().trim();
        String cc = etCc.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String body = etBody.getText().toString().trim();

        // Validate required fields
        if (to.isEmpty()) {
            etTo.setError("Please enter at least one recipient");
            etTo.requestFocus();
            return;
        }

        if (subject.isEmpty()) {
            etSubject.setError("Please enter a subject");
            etSubject.requestFocus();
            return;
        }

        if (body.isEmpty()) {
            etBody.setError("Please enter a message");
            etBody.requestFocus();
            return;
        }

        // Clear any previous errors
        etTo.setError(null);
        etSubject.setError(null);
        etBody.setError(null);

        // Parse recipients
        List<String> toList = parseUsernames(to);
        List<String> ccList = cc.isEmpty() ? null : parseUsernames(cc);

        // Send email through ViewModel
        viewModel.sendEmail(toList, ccList, subject, body);
    }

    private void saveAsDraftAndClose() {
        // Check if there's content to save
        String to = etTo.getText().toString().trim();
        String cc = etCc.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String body = etBody.getText().toString().trim();

        if (!to.isEmpty() || !cc.isEmpty() || !subject.isEmpty() || !body.isEmpty()) {
            // Save as draft using the ViewModel
            List<String> toList = parseUsernames(to);
            List<String> ccList = cc.isEmpty() ? null : parseUsernames(cc);
            viewModel.saveAsDraft(toList, ccList, subject, body);
            Toast.makeText(this, "Draft saved", Toast.LENGTH_SHORT).show();
        }
        
        finish();
    }

    private void deleteWithoutSaving() {
        // Check if there's content
        String to = etTo.getText().toString().trim();
        String cc = etCc.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String body = etBody.getText().toString().trim();

        if (!to.isEmpty() || !cc.isEmpty() || !subject.isEmpty() || !body.isEmpty()) {
            // Show confirmation dialog
            Toast.makeText(this, "Message discarded", Toast.LENGTH_SHORT).show();
        }
        
        finish();
    }

    private List<String> parseUsernames(String usernameString) {
        // Split by comma and trim whitespace
        String[] usernames = usernameString.split(",");
        for (int i = 0; i < usernames.length; i++) {
            usernames[i] = usernames[i].trim();
        }
        return Arrays.asList(usernames);
    }

    @Override
    public void onBackPressed() {
        // Save as draft when back button is pressed (same as X button)
        saveAsDraftAndClose();
    }
} 