package com.example.eyo.ui.home;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.R;
import com.example.eyo.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import android.widget.ImageButton;
import android.widget.EditText;

import com.example.eyo.ui.auth.LoginActivity;
import com.example.eyo.utils.TokenManager;

public class HomeActivity extends AppCompatActivity {

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
        mailListManager = new MailListManager(mailsRecyclerView, emptyState, tokenManager);
        
        // Set navigation listener
        navigationView.setNavigationItemSelectedListener(navigationHandler);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
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
            Intent intent = new Intent(this, com.example.eyo.ui.compose.ComposeActivity.class);
            startActivity(intent);
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 