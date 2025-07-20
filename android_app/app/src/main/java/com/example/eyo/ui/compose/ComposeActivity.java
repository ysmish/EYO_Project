package com.example.eyo.ui.compose;

import android.content.Context;
import android.content.Intent;
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
import com.example.eyo.data.Mail;
import com.example.eyo.viewmodel.ComposeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComposeActivity extends AppCompatActivity {

    private static final String EXTRA_DRAFT_MAIL = "extra_draft_mail";
    public static final int RESULT_DRAFT_SAVED = 1001;
    public static final int RESULT_MAIL_SENT = 1002;
    public static final int RESULT_DRAFT_DELETED = 1003;

    private EditText etTo, etCc, etSubject, etBody;
    private ImageButton btnAddCc, btnRemoveCc;
    private ImageButton btnSend, btnCloseDraft, btnDelete;
    private LinearLayout layoutCc;
    private View dividerCc;
    
    private ComposeViewModel viewModel;
    private boolean isCcVisible = false;
    private boolean isWaitingForOperation = false;
    private Mail draftMail = null; // The draft being edited, if any

    /**
     * Creates an intent to open ComposeActivity for composing a new email
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, ComposeActivity.class);
    }

    /**
     * Creates an intent to open ComposeActivity for editing a draft email
     */
    public static Intent createIntentForDraft(Context context, Mail draftMail) {
        Intent intent = new Intent(context, ComposeActivity.class);
        intent.putExtra(EXTRA_DRAFT_MAIL, draftMail);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Check if we're editing a draft
        if (getIntent().hasExtra(EXTRA_DRAFT_MAIL)) {
            draftMail = (Mail) getIntent().getSerializableExtra(EXTRA_DRAFT_MAIL);
        }

        initViews();
        setupViewModel();
        setupClickListeners();
        setupCcFieldWatcher();
        observeViewModel();
        
        // Populate fields if editing a draft
        if (draftMail != null) {
            populateFieldsFromDraft();
        }
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
        // Clear any previous messages
        viewModel.clearMessages();
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
                // Disable all buttons during loading
                btnSend.setEnabled(false);
                btnCloseDraft.setEnabled(false);
                btnDelete.setEnabled(false);
            } else {
                // Re-enable all buttons when not loading
                btnSend.setEnabled(true);
                btnCloseDraft.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                android.util.Log.e("ComposeActivity", "Error message received: " + error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                // Reset waiting flag on error
                isWaitingForOperation = false;
            }
        });

        viewModel.getSuccessMessage().observe(this, success -> {
            if (success != null) {
                Toast.makeText(this, success, Toast.LENGTH_SHORT).show();
                
                // Close activity after any successful operation with proper result
                if (isWaitingForOperation) {
                    // Determine the result based on the success message
                    if (success.contains("sent") || success.contains("Sent")) {
                        setResult(RESULT_MAIL_SENT);
                    } else if (success.contains("deleted")) {
                        setResult(RESULT_DRAFT_DELETED);
                    } else {
                        setResult(RESULT_DRAFT_SAVED);
                    }
                    finish();
                }
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

        // Send email through ViewModel and wait for completion
        isWaitingForOperation = true;
        
        if (draftMail != null) {
            // Sending an existing draft
            viewModel.sendEmail(toList, ccList, subject, body, draftMail.getId());
        } else {
            // Sending a new email
            viewModel.sendEmail(toList, ccList, subject, body);
        }
    }

    private void saveAsDraftAndClose() {
        // Check if there's content to save
        String to = etTo.getText().toString().trim();
        String cc = etCc.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String body = etBody.getText().toString().trim();

        android.util.Log.d("ComposeActivity", "saveAsDraftAndClose - to: '" + to + "', cc: '" + cc + "', subject: '" + subject + "', body: '" + body + "'");

        if (!to.isEmpty() || !cc.isEmpty() || !subject.isEmpty() || !body.isEmpty()) {
            // Save as draft using the ViewModel and wait for completion
            isWaitingForOperation = true;
            List<String> toList = parseUsernames(to);
            List<String> ccList = cc.isEmpty() ? null : parseUsernames(cc);
            
            android.util.Log.d("ComposeActivity", "saveAsDraftAndClose - toList: " + toList + ", ccList: " + ccList);
            
            if (draftMail != null) {
                // We're editing an existing draft - update it instead of creating new
                android.util.Log.d("ComposeActivity", "saveAsDraftAndClose - updating existing draft: " + draftMail.getId());
                viewModel.updateDraft(draftMail.getId(), toList, ccList, subject, body);
            } else {
                // We're creating a new draft
                android.util.Log.d("ComposeActivity", "saveAsDraftAndClose - creating new draft");
                viewModel.saveAsDraft(toList, ccList, subject, body);
            }
        } else {
            // No content to save, just close
            android.util.Log.d("ComposeActivity", "saveAsDraftAndClose - no content to save, closing");
            finish();
        }
    }

    private void deleteWithoutSaving() {
        // Check if we're editing an existing draft
        if (draftMail != null) {
            // Delete the existing draft from server
            isWaitingForOperation = true;
            viewModel.deleteDraft(draftMail.getId());
        } else {
            // Check if there's content for a new draft
            String to = etTo.getText().toString().trim();
            String cc = etCc.getText().toString().trim();
            String subject = etSubject.getText().toString().trim();
            String body = etBody.getText().toString().trim();

            if (!to.isEmpty() || !cc.isEmpty() || !subject.isEmpty() || !body.isEmpty()) {
                Toast.makeText(this, "Message discarded", Toast.LENGTH_SHORT).show();
            }
            
            // No server call needed for new draft, just close
            finish();
        }
    }

    private List<String> parseUsernames(String usernameString) {
        if (usernameString == null || usernameString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Split by comma and trim whitespace
        String[] usernames = usernameString.split(",");
        List<String> result = new ArrayList<>();
        
        for (String username : usernames) {
            String trimmed = username.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        
        return result;
    }

    private void populateFieldsFromDraft() {
        if (draftMail == null) return;
        
        // Populate TO field
        if (draftMail.getTo() != null && !draftMail.getTo().isEmpty()) {
            String toText = String.join(", ", draftMail.getTo());
            etTo.setText(toText);
        }
        
        // Populate CC field if it has content
        if (draftMail.getCc() != null && !draftMail.getCc().isEmpty()) {
            String ccText = String.join(", ", draftMail.getCc());
            etCc.setText(ccText);
            // Show CC field since it has content
            toggleCcVisibility(true);
        }
        
        // Populate subject
        if (draftMail.getSubject() != null && !draftMail.getSubject().isEmpty()) {
            etSubject.setText(draftMail.getSubject());
        }
        
        // Populate body
        if (draftMail.getBody() != null && !draftMail.getBody().isEmpty()) {
            etBody.setText(draftMail.getBody());
        }
    }

    @Override
    public void onBackPressed() {
        // Save as draft when back button is pressed (same as X button)
        saveAsDraftAndClose();
        // Don't call super.onBackPressed() here - let the operation complete first
    }
} 