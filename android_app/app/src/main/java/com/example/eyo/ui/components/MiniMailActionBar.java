package com.example.eyo.ui.components;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.R;
import com.example.eyo.data.ApiService;
import com.example.eyo.data.Label;
import com.example.eyo.data.Mail;
import com.example.eyo.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class MiniMailActionBar extends LinearLayout {

    private ImageButton btnReportSpam;
    private ImageButton btnLabel;
    private ImageButton btnDelete;
    private TextView tvStatus;
    
    private Mail currentMail;
    private OnActionListener onActionListener;
    private TokenManager tokenManager;
    private List<Label> availableLabels = new ArrayList<>();
    
    public interface OnActionListener {
        void onMailUpdated(Mail mail);
        void onMailDeleted(Mail mail);
        void onError(String error);
    }
    
    public MiniMailActionBar(Context context) {
        super(context);
        init(context);
    }
    
    public MiniMailActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public MiniMailActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.mini_mail_action_bar, this, true);
        
        btnReportSpam = findViewById(R.id.btn_report_spam);
        btnLabel = findViewById(R.id.btn_label);
        btnDelete = findViewById(R.id.btn_delete);
        tvStatus = findViewById(R.id.tv_status);
        
        tokenManager = TokenManager.getInstance(context);
        
        setupClickListeners();
        loadLabels();
    }
    
    private void setupClickListeners() {
        btnReportSpam.setOnClickListener(v -> toggleSpamStatus());
        btnLabel.setOnClickListener(v -> showLabelDialog());
        btnDelete.setOnClickListener(v -> deleteMail());
    }
    
    public void setMail(Mail mail) {
        this.currentMail = mail;
        updateSpamButtonState();
    }
    
    public void setOnActionListener(OnActionListener listener) {
        this.onActionListener = listener;
    }
    
    private void updateSpamButtonState() {
        if (currentMail != null) {
            boolean isSpam = currentMail.isSpam();
            // Change button appearance based on spam status
            if (isSpam) {
                btnReportSpam.setImageResource(R.drawable.ic_verify);
                btnReportSpam.setContentDescription("Remove from spam");
            } else {
                btnReportSpam.setImageResource(R.drawable.ic_report);
                btnReportSpam.setContentDescription("Report as spam");
            }
        }
    }
    
    private void toggleSpamStatus() {
        if (currentMail == null) return;
        
        boolean isCurrentlySpam = currentMail.isSpam();
        boolean reportAsSpam = !isCurrentlySpam;
        
        showStatus("Updating spam status...");
        
        String authToken = tokenManager.getBearerToken();
        if (authToken == null) {
            showError("Authentication required");
            return;
        }
        
        ApiService.updateMail(currentMail.getId(), reportAsSpam, authToken, new ApiService.ApiCallback<>() {
            @Override
            public void onSuccess(String result) {
                // Update mail object
                List<String> labels = new ArrayList<>(currentMail.getLabels());
                if (reportAsSpam) {
                    if (!labels.contains("5") && !labels.contains("Spam")) {
                        labels.add("5"); // Spam label ID
                    }
                    labels.remove("1"); // Remove Inbox label
                    labels.remove("Inbox");
                } else {
                    labels.remove("5"); // Remove Spam label
                    labels.remove("Spam");
                    if (!labels.contains("1") && !labels.contains("Inbox")) {
                        labels.add("1"); // Add Inbox label
                    }
                }
                currentMail.setLabels(labels);

                updateSpamButtonState();
                showStatus(reportAsSpam ? "Reported as spam" : "Removed from spam");
                hideStatusAfterDelay();

                if (onActionListener != null) {
                    onActionListener.onMailUpdated(currentMail);
                }
            }

            @Override
            public void onError(String error) {
                showError("Failed to update spam status: " + error);
            }
        });
    }
    
    private void showLabelDialog() {
        if (currentMail == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_label_selection, null);
        
        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_labels);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        LabelSelectionAdapter adapter = new LabelSelectionAdapter((label, isSelected) -> {
            // This will be handled when dialog is dismissed
        });
        
        adapter.setLabels(availableLabels);
        adapter.setAppliedLabels(currentMail.getLabels());
        recyclerView.setAdapter(adapter);
        
        List<String> modifiedLabels = new ArrayList<>(currentMail.getLabels());
        
        adapter.setOnLabelToggleListener((label, isSelected) -> {
            String labelId = String.valueOf(label.getId());
            if (isSelected) {
                if (!modifiedLabels.contains(labelId) && !modifiedLabels.contains(label.getName())) {
                    modifiedLabels.add(labelId);
                }
            } else {
                modifiedLabels.remove(labelId);
                modifiedLabels.remove(label.getName());
            }
        });
        
        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("Manage Labels")
                .setPositiveButton("Apply", (dialogInterface, which) -> updateMailLabels(modifiedLabels))
                .setNegativeButton("Cancel", null)
                .create();
        
        dialog.show();
        
        // Set custom button colors for dark mode compatibility
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.dialog_button_text));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.dialog_button_text));
    }
    
    private void updateMailLabels(List<String> newLabels) {
        if (currentMail == null) return;
        
        showStatus("Updating labels...");
        
        String authToken = tokenManager.getBearerToken();
        if (authToken == null) {
            showError("Authentication required");
            return;
        }
        
        ApiService.updateMailLabels(currentMail.getId(), newLabels, authToken, new ApiService.ApiCallback<>() {
            @Override
            public void onSuccess(String result) {
                // Update mail object
                currentMail.setLabels(newLabels);

                showStatus("Labels updated");
                hideStatusAfterDelay();

                if (onActionListener != null) {
                    onActionListener.onMailUpdated(currentMail);
                }
            }

            @Override
            public void onError(String error) {
                showError("Failed to update labels: " + error);
            }
        });
    }
    
    private void deleteMail() {
        if (currentMail == null) return;
        
        AlertDialog deleteDialog = new AlertDialog.Builder(getContext())
                .setTitle("Delete Mail")
                .setMessage("Are you sure you want to delete this mail?")
                .setPositiveButton("Delete", (dialog, which) -> performDelete())
                .setNegativeButton("Cancel", null)
                .create();
        
        deleteDialog.show();
        
        // Set custom button colors for dark mode compatibility
        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.dialog_button_text));
        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.dialog_button_text));
    }
    
    private void performDelete() {
        showStatus("Deleting mail...");
        
        String authToken = tokenManager.getBearerToken();
        if (authToken == null) {
            showError("Authentication required");
            return;
        }
        
        ApiService.deleteMail(currentMail.getId(), authToken, new ApiService.ApiCallback<>() {
            @Override
            public void onSuccess(String result) {
                showStatus("Mail deleted");
                hideStatusAfterDelay();

                if (onActionListener != null) {
                    onActionListener.onMailDeleted(currentMail);
                }
            }

            @Override
            public void onError(String error) {
                showError("Failed to delete mail: " + error);
            }
        });
    }
    
    private void loadLabels() {
        String authToken = tokenManager.getBearerToken();
        if (authToken == null) return;
        
        ApiService.getLabels(authToken, new ApiService.ApiCallback<>() {
            @Override
            public void onSuccess(List<Label> labels) {
                availableLabels = labels != null ? labels : new ArrayList<>();
            }

            @Override
            public void onError(String error) {
                // Labels loading is optional for the component to work
                availableLabels = new ArrayList<>();
            }
        });
    }
    
    private void showStatus(String message) {
        tvStatus.setText(message);
        tvStatus.setVisibility(VISIBLE);
    }
    
    private void showError(String error) {
        if (onActionListener != null) {
            onActionListener.onError(error);
        } else {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void hideStatusAfterDelay() {
        postDelayed(() -> tvStatus.setVisibility(GONE), 3000);
    }
}
