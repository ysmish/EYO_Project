package com.example.eyo.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.R;
import com.example.eyo.data.Mail;
import com.example.eyo.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import android.widget.ImageButton;
import android.widget.EditText;

import java.util.List;
import java.util.ArrayList;
import android.content.Intent;
import com.example.eyo.ui.auth.LoginActivity;
import com.example.eyo.utils.TokenManager;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CREATE_LABEL = 100;
    private static final int REQUEST_CODE_MAIL_DETAIL = 1001;
    private static final int REQUEST_CODE_COMPOSE = 1002;


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnSidebar;
    private ImageButton btnProfile;
    private FloatingActionButton fabCompose;
    private RecyclerView mailsRecyclerView;
    private View emptyState;
    private EditText searchEditText;
    private TextView categoryTitle;
    
    private HomeViewModel viewModel;
    private MailAdapter mailAdapter;
    
    // Manager classes for organized code
    private ProfileDialogManager profileDialogManager;
    private NavigationHandler navigationHandler;
    private SearchHandler searchHandler;
    private MailListManager mailListManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is logged in
        TokenManager tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isLoggedIn()) {
            // Redirect to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_home);

        initViews();
        setupViewModel();
        setupManagers();
        setupClickListeners();
        observeViewModel();
        
        // Set default navigation item and load inbox mails
        navigationView.setCheckedItem(R.id.nav_inbox);
        viewModel.navigateToInbox(); // Load initial inbox mails
        
        // Load user labels for the navigation menu
        viewModel.loadUserLabels();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        btnSidebar = findViewById(R.id.btn_sidebar);
        btnProfile = findViewById(R.id.btn_profile);
        fabCompose = findViewById(R.id.fab_compose);
        mailsRecyclerView = findViewById(R.id.mails_recycler_view);
        emptyState = findViewById(R.id.empty_state);
        searchEditText = findViewById(R.id.search_edit_text);
        categoryTitle = findViewById(R.id.tv_category_title);
    }

    private void setupManagers() {
        // Get TokenManager instance
        TokenManager tokenManager = TokenManager.getInstance(this);
        
        // Initialize manager classes
        profileDialogManager = new ProfileDialogManager(this);
        navigationHandler = new NavigationHandler(this, viewModel, navigationView, drawerLayout);
        searchHandler = new SearchHandler(viewModel, searchEditText);
        mailListManager = new MailListManager(mailsRecyclerView, emptyState, tokenManager, mailAdapter);
        
        // Set navigation listener
        navigationView.setNavigationItemSelectedListener(navigationHandler);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        // Initialize mail adapter
        mailAdapter = new MailAdapter(new MailAdapter.OnMailClickListener() {
            @Override
            public void onMailClick(Mail mail) {
                // Mark mail as read if it's not already read and not a draft
                if (!mail.isRead() && !mail.isDraft()) {
                    TokenManager tokenManager = TokenManager.getInstance(HomeActivity.this);
                    String token = tokenManager.getBearerToken();
                    if (token != null) {
                        com.example.eyo.data.ApiService.updateMailReadStatus(mail.getId(), true, token, new com.example.eyo.data.ApiService.ApiCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Update the mail object to reflect read status
                                mail.setRead(true);
                                // Refresh the adapter to show updated read status
                                mailAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(String error) {
                                // Continue with opening the mail even if read status update fails
                                Log.e("HomeActivity", "Failed to mark mail as read: " + error);
                            }
                        });
                    }
                }
                
                // Check if this is a draft mail
                if (mail.isDraft()) {
                    // Open compose activity for editing draft
                    Intent intent = com.example.eyo.ui.compose.ComposeActivity.createIntentForDraft(HomeActivity.this, mail);
                    startActivityForResult(intent, REQUEST_CODE_COMPOSE);
                } else {
                    // Open mail detail activity for regular mails
                    Intent intent = com.example.eyo.ui.mail.MailDetailActivity.createIntent(HomeActivity.this, mail);
                    startActivityForResult(intent, REQUEST_CODE_MAIL_DETAIL);
                }
            }
            
            @Override
            public void onStarClick(Mail mail) {
                toggleStarStatus(mail);
            }
        });
        
        // Setup RecyclerView
        mailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mailsRecyclerView.setAdapter(mailAdapter);
    }

    private void setupClickListeners() {
        // Sidebar button click
        btnSidebar.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        
        fabCompose.setOnClickListener(v -> {
            // Open ComposeActivity
            Intent intent = com.example.eyo.ui.compose.ComposeActivity.createIntent(this);
            startActivityForResult(intent, REQUEST_CODE_COMPOSE);
        });
        
        // Profile button click - delegate to ProfileDialogManager
        btnProfile.setOnClickListener(v -> {
            profileDialogManager.showUserProfileDialog(viewModel.getCurrentUser().getValue());
        });

    }

    private void observeViewModel() {
        viewModel.getCurrentFilter().observe(this, filter -> {
            // Filter changed - navigation handles the mail loading
            // No need to manually show empty state here
        });
        
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                emptyState.setVisibility(View.GONE);
                mailsRecyclerView.setVisibility(View.GONE);
                // TODO: Show loading indicator
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getSearchQuery().observe(this, query -> {
            // Delegate to SearchHandler
            searchHandler.updateSearchQuery(query);
        });
        
        viewModel.getMails().observe(this, mails -> {
            // Delegate to MailListManager
            mailListManager.updateMailsList(mails);
        });
        
        viewModel.getCategoryTitle().observe(this, title -> {
            if (title != null) {
                categoryTitle.setText(title);
            }
        });
        
        viewModel.getCurrentFilter().observe(this, filter -> {
            // Delegate to MailListManager
            mailListManager.setCurrentCategory(filter);
        });
        
        viewModel.getUserLabels().observe(this, labels -> {
            if (labels != null) {
                // Delegate to NavigationHandler
                navigationHandler.populateLabelsInNavigationMenu(labels);
            }
        });
        
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // Delegate to ProfileDialogManager
                profileDialogManager.updateProfileButton(user, btnProfile);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Check if this is the result from CreateLabelActivity
        if (requestCode == REQUEST_CODE_CREATE_LABEL && resultCode == RESULT_OK) {
            // Add a small delay to ensure the server has processed the label creation
            // then reload labels to show the newly created label
            new android.os.Handler().postDelayed(() -> {
                viewModel.loadUserLabels();
            }, 500); // 500ms delay
        }
        
        if (requestCode == REQUEST_CODE_MAIL_DETAIL && resultCode == RESULT_OK) {
            // Mail was deleted or updated, refresh the mail list
            viewModel.loadUserLabels(); // Refresh labels if needed
            viewModel.refreshCurrentCategory(); // Refresh the current category mails
        }
        
        if (requestCode == REQUEST_CODE_COMPOSE && (resultCode == com.example.eyo.ui.compose.ComposeActivity.RESULT_DRAFT_SAVED || 
                                                   resultCode == com.example.eyo.ui.compose.ComposeActivity.RESULT_MAIL_SENT ||
                                                   resultCode == com.example.eyo.ui.compose.ComposeActivity.RESULT_DRAFT_DELETED)) {
            // Draft was saved, mail was sent, or draft was deleted, refresh the mail list
            viewModel.loadUserLabels(); // Refresh labels if needed
            viewModel.refreshCurrentCategory(); // Refresh the current category mails
        }
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void toggleStarStatus(Mail mail) {
        if (mail == null) return;
        
        List<String> labels = new ArrayList<>(mail.getLabels());
        boolean isStarred = mail.isStarred();
        
        if (isStarred) {
            labels.remove("Starred");
        } else {
            labels.add("Starred");
        }
        
        // Update mail labels via API
        TokenManager tokenManager = TokenManager.getInstance(this);
        String token = tokenManager.getBearerToken();
        if (token != null) {
            com.example.eyo.data.ApiService.updateMailLabels(mail.getId(), labels, token, new com.example.eyo.data.ApiService.ApiCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    // Update the mail object
                    mail.setLabels(labels);
                    // Refresh the adapter to show updated star state
                    mailAdapter.notifyDataSetChanged();
                    Toast.makeText(HomeActivity.this, 
                        isStarred ? "Removed from starred" : "Added to starred", 
                        Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(HomeActivity.this, "Error updating star: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show();
        }
    }
} 