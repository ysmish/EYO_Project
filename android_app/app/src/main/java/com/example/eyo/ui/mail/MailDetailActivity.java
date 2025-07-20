package com.example.eyo.ui.mail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eyo.R;
import com.example.eyo.data.Mail;
import com.example.eyo.data.Label;
import com.example.eyo.data.User;
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
    private ImageView ivUserAvatar;
    private ImageButton btnStar;
    
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
        ivUserAvatar = findViewById(R.id.iv_user_avatar);
        btnStar = findViewById(R.id.btn_star);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            // Set result to indicate activity finished (for potential list refresh)
            setResult(RESULT_OK);
            finish();
        });
        
        // Star button listener
        btnStar.setOnClickListener(v -> toggleStarStatus());
        
        // Set up mini action bar listener
        miniActionBar.setOnActionListener(new MiniMailActionBar.OnActionListener() {
            @Override
            public void onMailUpdated(Mail mail) {
                currentMail = mail;
                updateMailDisplay();
                Toast.makeText(MailDetailActivity.this, "Mail updated successfully", Toast.LENGTH_SHORT).show();
                // Set result to trigger refresh in calling activity
                setResult(RESULT_OK);
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
        
        // Update user avatar
        updateUserAvatar(from);
        
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
        
        // Update star button state
        updateStarButtonState();
        
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
        Log.d("MailDetailActivity", "updateLabelChipsDisplay - mail labels: " + labelsList);
        Log.d("MailDetailActivity", "updateLabelChipsDisplay - available labels count: " + availableLabels.size());
        
        if (labelsList != null && !labelsList.isEmpty()) {
            // Show ALL labels including system labels and custom labels
            List<String> allLabels = new ArrayList<>(labelsList);
            
            Log.d("MailDetailActivity", "updateLabelChipsDisplay - showing labels: " + allLabels);
            
            if (!allLabels.isEmpty()) {
                layoutLabels.setVisibility(View.VISIBLE);
                
                // If we have available labels with colors, use proper chip display
                if (!availableLabels.isEmpty()) {
                    Log.d("MailDetailActivity", "Using proper chip display with " + availableLabels.size() + " available labels");
                    chipsLabels.setLabels(availableLabels);
                    chipsLabels.setAppliedLabels(allLabels);
                } else {
                    // Fall back to simple chips if labels not loaded yet
                    Log.d("MailDetailActivity", "Using simple chips fallback for " + allLabels.size() + " labels");
                    chipsLabels.showSimpleChips(allLabels);
                }
                
                // Update star button state after labels are displayed
                updateStarButtonState();
            } else {
                layoutLabels.setVisibility(View.GONE);
            }
        } else {
            layoutLabels.setVisibility(View.GONE);
        }
    }
    
    private void updateUserAvatar(String fromEmail) {
        if (fromEmail == null || fromEmail.isEmpty()) {
            // Set default avatar
            ivUserAvatar.setImageResource(R.drawable.ic_person);
            return;
        }
        
        // Extract username from email (remove @eyo.com if present)
        String username = fromEmail;
        if (fromEmail.contains("@")) {
            username = fromEmail.substring(0, fromEmail.indexOf("@"));
        }
        
        // Set default image while loading
        ivUserAvatar.setImageResource(R.drawable.ic_person);
        
        // Load user data from API if we have token
        TokenManager tokenManager = TokenManager.getInstance(this);
        String authToken = tokenManager.getBearerToken();
        
        if (authToken != null) {
            ApiService.getUserData(username, authToken, new ApiService.ApiCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    // Load avatar image using Glide
                    loadAvatarImage(user, ivUserAvatar);
                }
                
                @Override
                public void onError(String error) {
                    // Keep default image on error
                    ivUserAvatar.setImageResource(R.drawable.ic_person);
                }
            });
        } else {
            // No token available, use default
            ivUserAvatar.setImageResource(R.drawable.ic_person);
        }
    }
    
    private void loadAvatarImage(User user, ImageView avatarImageView) {
        if (user != null && user.getPhoto() != null && !user.getPhoto().isEmpty()) {
            // Load user's profile picture using Glide
            Glide.with(this)
                    .load(user.getPhoto())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(avatarImageView);
        } else {
            // Use default avatar
            avatarImageView.setImageResource(R.drawable.ic_person);
        }
    }

        private void toggleStarStatus() {
        if (currentMail == null) return;
        
        List<String> labels = new ArrayList<>(currentMail.getLabels());
        boolean isStarred = currentMail.isStarred();
        
        if (isStarred) {
            // Remove Starred label (ID 3)
            labels.remove("3");
            labels.remove("Starred"); // Also remove string version if present
        } else {
            // Add Starred label (ID 3)
            labels.add("3");
        }

        // Update mail labels via API
        TokenManager tokenManager = TokenManager.getInstance(this);
        String token = tokenManager.getBearerToken();
        if (token != null) {
            ApiService.updateMailLabels(currentMail.getId(), labels, token, new ApiService.ApiCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    // Update the mail object
                    currentMail.setLabels(labels);
                    
                    // Refresh the chips display to show the updated labels
                    updateLabelChipsDisplay();
                    
                    // Update star button state
                    updateStarButtonState();
                    
                    Toast.makeText(MailDetailActivity.this,
                            isStarred ? "Removed from starred" : "Added to starred",
                            Toast.LENGTH_SHORT).show();
                    
                    // Set result to indicate mail was updated
                    setResult(RESULT_OK);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MailDetailActivity.this, "Error updating star: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
        }
    }

        private void updateStarButtonState() {
        if (currentMail == null) return;
        
        boolean isStarred = currentMail.isStarred();
        
        Log.d("MailDetailActivity", "updateStarButtonState - labels: " + currentMail.getLabels() + ", isStarred: " + isStarred);
        
        if (isStarred) {
            btnStar.setImageResource(R.drawable.ic_star_filled);
            btnStar.setContentDescription("Remove from starred");
            Log.d("MailDetailActivity", "Star button set to FILLED");
        } else {
            btnStar.setImageResource(R.drawable.ic_star);
            btnStar.setContentDescription("Add to starred");
            Log.d("MailDetailActivity", "Star button set to EMPTY");
        }
    }
    
    @Override
    public void onBackPressed() {
        // Set result to indicate activity finished (for potential list refresh)
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
