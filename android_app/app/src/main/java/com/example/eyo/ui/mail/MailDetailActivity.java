package com.example.eyo.ui.mail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eyo.R;
import com.example.eyo.data.Mail;
import com.example.eyo.data.Label;
import com.example.eyo.data.requests.GetLabelsRequest;
import com.example.eyo.data.ApiService;
import com.example.eyo.utils.TokenManager;
import com.example.eyo.ui.components.MiniMailActionBar;
import com.example.eyo.ui.components.LabelChipsContainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MailDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_MAIL_ID = "extra_mail_id";
    public static final String EXTRA_MAIL_OBJECT = "extra_mail_object";
    
    // UI Components
    private ImageButton btnBack;
    private TextView tvSubject;
    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvCc;
    private TextView tvDate;
    private LabelChipsContainer chipsLabels;
    private TextView tvBody;
    private TextView tvAttachments;
    private LinearLayout layoutTo;
    private LinearLayout layoutCc;
    private LinearLayout layoutLabels;
    private androidx.cardview.widget.CardView cardAttachments;
    private MiniMailActionBar miniActionBar;
    
    private Mail currentMail;
    private List<Label> availableLabels = new ArrayList<>();
    
    /**
     * Static method to create intent for this activity
     */
    public static Intent createIntent(Context context, Mail mail) {
        Intent intent = new Intent(context, MailDetailActivity.class);
        intent.putExtra(EXTRA_MAIL_OBJECT, mail);
        return intent;
    }
    
    /**
     * Static method to create intent with mail ID (for future use with API calls)
     */
    public static Intent createIntent(Context context, int mailId) {
        Intent intent = new Intent(context, MailDetailActivity.class);
        intent.putExtra(EXTRA_MAIL_ID, mailId);
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_detail);
        
        initializeViews();
        setupListeners();
        loadLabels();
        loadMailData();
    }
    
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvSubject = findViewById(R.id.tv_subject);
        tvFrom = findViewById(R.id.tv_from);
        tvTo = findViewById(R.id.tv_to);
        tvCc = findViewById(R.id.tv_cc);
        tvDate = findViewById(R.id.tv_date);
        chipsLabels = findViewById(R.id.chips_labels);
        tvBody = findViewById(R.id.tv_body);
        tvAttachments = findViewById(R.id.tv_attachments);
        layoutTo = findViewById(R.id.layout_to);
        layoutCc = findViewById(R.id.layout_cc);
        layoutLabels = findViewById(R.id.layout_labels);
        cardAttachments = findViewById(R.id.card_attachments);
        miniActionBar = findViewById(R.id.mini_action_bar);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        // Set up mini action bar listener
        miniActionBar.setOnActionListener(new MiniMailActionBar.OnActionListener() {
            @Override
            public void onMailUpdated(Mail mail) {
                currentMail = mail;
                updateMailDisplay();
                Toast.makeText(MailDetailActivity.this, "Mail updated successfully", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onMailDeleted(Mail mail) {
                Toast.makeText(MailDetailActivity.this, "Mail deleted successfully", Toast.LENGTH_SHORT).show();
                // Set result and finish activity
                setResult(RESULT_OK);
                finish();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(MailDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void loadLabels() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        String token = tokenManager.getToken();
        if (token != null) {
            GetLabelsRequest request = new GetLabelsRequest(token);
            ApiService.getLabels(token, new ApiService.ApiCallback<List<Label>>() {
                @Override
                public void onSuccess(List<Label> result) {
                    availableLabels = result != null ? result : new ArrayList<>();
                    // Update chips display if mail is already loaded
                    if (currentMail != null) {
                        updateLabelChipsDisplay();
                    }
                }

                @Override
                public void onError(String error) {
                    // If labels loading fails, fall back to simple chips
                    availableLabels = new ArrayList<>();
                    if (currentMail != null) {
                        updateLabelChipsDisplay();
                    }
                }
            });
        }
    }
    
    private void loadMailData() {
        Intent intent = getIntent();
        
        // First try to get Mail object directly
        if (intent.hasExtra(EXTRA_MAIL_OBJECT)) {
            currentMail = (Mail) intent.getSerializableExtra(EXTRA_MAIL_OBJECT);
            if (currentMail != null) {
                displayMail(currentMail);
                return;
            }
        }
        
        // If no Mail object, try to get mail ID and load from API
        if (intent.hasExtra(EXTRA_MAIL_ID)) {
            int mailId = intent.getIntExtra(EXTRA_MAIL_ID, -1);
            if (mailId != -1) {
                loadMailFromApi(mailId);
                return;
            }
        }
        
        // If we get here, no valid mail data was provided
        Toast.makeText(this, "Error: No mail data provided", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void loadMailFromApi(int mailId) {
        TokenManager tokenManager = TokenManager.getInstance(this);
        String token = tokenManager.getToken();
        if (token != null) {
            ApiService.getMail(mailId, token, new ApiService.ApiCallback<Mail>() {
                @Override
                public void onSuccess(Mail result) {
                    if (result != null) {
                        currentMail = result;
                        displayMail(currentMail);
                    } else {
                        Toast.makeText(MailDetailActivity.this, "Error: Mail not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MailDetailActivity.this, "Error loading mail: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Error: No authentication token", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void displayMail(Mail mail) {
        if (mail == null) return;
        
        // Set mail in mini action bar
        miniActionBar.setMail(mail);
        
        // Display mail content
        updateMailDisplay();
    }
    
    private void updateMailDisplay() {
        if (currentMail == null) return;
        
        // Subject
        String subject = currentMail.getSubject();
        tvSubject.setText(subject != null && !subject.isEmpty() ? subject : "No Subject");
        
        // From
        String from = currentMail.getFrom();
        tvFrom.setText(from != null ? from : "Unknown Sender");
        
        // To recipients
        List<String> toList = currentMail.getTo();
        if (toList != null && !toList.isEmpty()) {
            layoutTo.setVisibility(View.VISIBLE);
            tvTo.setText(String.join(", ", toList));
        } else {
            layoutTo.setVisibility(View.GONE);
        }
        
        // CC recipients
        List<String> ccList = currentMail.getCc();
        if (ccList != null && !ccList.isEmpty()) {
            layoutCc.setVisibility(View.VISIBLE);
            tvCc.setText(String.join(", ", ccList));
        } else {
            layoutCc.setVisibility(View.GONE);
        }
        
        // Date
        if (currentMail.getDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault());
            tvDate.setText(dateFormat.format(currentMail.getDate()));
        } else {
            tvDate.setText("Unknown Date");
        }
        
        // Labels - Display as chips
        updateLabelChipsDisplay();
        
        // Body
        String body = currentMail.getBody();
        tvBody.setText(body != null && !body.isEmpty() ? body : "This message has no content.");
        
        // Attachments
        List<String> attachmentsList = currentMail.getAttachments();
        if (attachmentsList != null && !attachmentsList.isEmpty()) {
            cardAttachments.setVisibility(View.VISIBLE);
            StringBuilder attachmentsText = new StringBuilder();
            for (int i = 0; i < attachmentsList.size(); i++) {
                if (i > 0) {
                    attachmentsText.append("\n");
                }
                attachmentsText.append("📎 ").append(attachmentsList.get(i));
            }
            tvAttachments.setText(attachmentsText.toString());
        } else {
            cardAttachments.setVisibility(View.GONE);
        }
    }
    
    private void updateLabelChipsDisplay() {
        if (currentMail == null) return;
        
        List<String> labelsList = currentMail.getLabels();
        if (labelsList != null && !labelsList.isEmpty()) {
            // Filter out system labels and show only meaningful ones
            List<String> meaningfulLabels = new ArrayList<>();
            for (String label : labelsList) {
                // Skip numeric system labels
                if (!label.matches("\\d+")) {
                    meaningfulLabels.add(label);
                }
            }
            
            if (!meaningfulLabels.isEmpty()) {
                layoutLabels.setVisibility(View.VISIBLE);
                
                // If we have available labels with colors, use proper chip display
                if (!availableLabels.isEmpty()) {
                    chipsLabels.setAvailableLabels(availableLabels);
                    chipsLabels.setAppliedLabels(meaningfulLabels);
                } else {
                    // Fall back to simple chips if labels not loaded yet
                    chipsLabels.showSimpleChips(meaningfulLabels);
                }
            } else {
                layoutLabels.setVisibility(View.GONE);
            }
        } else {
            layoutLabels.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onBackPressed() {
        // Handle back button press
        super.onBackPressed();
        finish();
    }
}
