package com.example.eyo.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eyo.R;
import com.example.eyo.data.Label;
import com.example.eyo.data.Mail;
import com.example.eyo.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import android.widget.ImageButton;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

import java.util.List;
import android.content.Intent;
import com.example.eyo.ui.auth.LoginActivity;
import com.example.eyo.utils.TokenManager;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_MAIL_DETAIL = 1001;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnSidebar;
    private FloatingActionButton fabCompose;
    private RecyclerView mailsRecyclerView;
    private View emptyState;
    private EditText searchEditText;
    private TextView categoryTitle;
    
    private HomeViewModel viewModel;
    private MailAdapter mailAdapter;
    private List<Label> userLabels;

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
        setupNavigationDrawer();
        setupViewModel();
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
        fabCompose = findViewById(R.id.fab_compose);
        mailsRecyclerView = findViewById(R.id.mails_recycler_view);
        emptyState = findViewById(R.id.empty_state);
        searchEditText = findViewById(R.id.search_edit_text);
        categoryTitle = findViewById(R.id.tv_category_title);
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        
        // Update navigation header with user info
        updateNavigationHeader();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        // Initialize mail adapter
        mailAdapter = new MailAdapter(new MailAdapter.OnMailClickListener() {
            @Override
            public void onMailClick(Mail mail) {
                // Open mail detail activity
                Intent intent = com.example.eyo.ui.mail.MailDetailActivity.createIntent(HomeActivity.this, mail);
                startActivityForResult(intent, REQUEST_CODE_MAIL_DETAIL);
            }
            
            @Override
            public void onStarClick(Mail mail) {
                // TODO: Handle star click (toggle star)
                Toast.makeText(HomeActivity.this, "Star clicked for: " + mail.getSubject(), Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(this, com.example.eyo.ui.compose.ComposeActivity.class);
            startActivity(intent);
        });
        
        // Search functionality
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            viewModel.performSearch(query);
            return true;
        });
        
        // Add text change listener for real-time search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                viewModel.performSearch(query);
            }
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
            // Update search edit text when query changes (e.g., cleared by navigation)
            if (query != null && !searchEditText.getText().toString().equals(query)) {
                searchEditText.setText(query);
                // Move cursor to end if setting text
                searchEditText.setSelection(query.length());
            }
        });
        
        viewModel.getMails().observe(this, mails -> {
            updateMailsList(mails);
        });
        
        viewModel.getCategoryTitle().observe(this, title -> {
            if (title != null) {
                categoryTitle.setText(title);
            }
        });
        
        viewModel.getCurrentFilter().observe(this, filter -> {
            if (filter != null && mailAdapter != null) {
                mailAdapter.setCurrentCategory(filter);
            }
        });
        
        viewModel.getUserLabels().observe(this, labels -> {
            if (labels != null) {
                populateLabelsInNavigationMenu(labels);
            }
        });
    }

    private void updateNavigationHeader() {
        // Logo is now handled in the layout - no dynamic updates needed
    }
    
    private void populateLabelsInNavigationMenu(List<Label> labels) {
        // Store labels for use in navigation item selection
        this.userLabels = labels;
        
        Menu menu = navigationView.getMenu();
        
        // Find the Labels submenu (assuming it's the second group after default mail folders)
        SubMenu labelsSubmenu = null;
        
        // Look for the "Labels" submenu by iterating through the menu
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                // Check if this is the labels submenu by looking for the example item
                SubMenu subMenu = item.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++) {
                    if (subMenu.getItem(j).getItemId() == R.id.nav_example) {
                        labelsSubmenu = subMenu;
                        break;
                    }
                }
                if (labelsSubmenu != null) break;
            }
        }
        
        if (labelsSubmenu != null) {
            // Clear existing label items (including the example)
            labelsSubmenu.clear();
            
            // Disable automatic icon tinting for the NavigationView to allow custom colors
            navigationView.setItemIconTintList(null);
            
            // Add user labels dynamically
            for (Label label : labels) {
                MenuItem labelItem = labelsSubmenu.add(Menu.NONE, View.generateViewId(), Menu.NONE, label.getName());
                
                // Create a colored icon for this label
                Drawable iconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_label);
                if (iconDrawable != null) {
                    iconDrawable = DrawableCompat.wrap(iconDrawable).mutate(); // Wrap and mutate for better compatibility
                    try {
                        int color = Color.parseColor(label.getColor());
                        DrawableCompat.setTint(iconDrawable, color);
                    } catch (IllegalArgumentException e) {
                        // If color parsing fails, use default color
                        DrawableCompat.setTint(iconDrawable, Color.parseColor("#4F46E5"));
                    }
                    labelItem.setIcon(iconDrawable);
                }
                
                labelItem.setCheckable(true);
            }
        }
    }

    private void updateMailsList(List<Mail> mails) {
        if (mails == null || mails.isEmpty()) {
            showEmptyState();
        } else {
            showMailList(mails);
        }
    }
    
    private void showEmptyState() {
        mailsRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        mailAdapter.clearMails();
    }
    
    private void showMailList(List<Mail> mails) {
        emptyState.setVisibility(View.GONE);
        mailsRecyclerView.setVisibility(View.VISIBLE);
        mailAdapter.setMails(mails);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_all) {
            viewModel.navigateToAll();
        } else if (id == R.id.nav_inbox) {
            viewModel.navigateToInbox();
        } else if (id == R.id.nav_sent) {
            viewModel.navigateToSent();
        } else if (id == R.id.nav_drafts) {
            viewModel.navigateToDrafts();
        } else if (id == R.id.nav_starred) {
            viewModel.navigateToStarred();
        } else if (id == R.id.nav_spam) {
            viewModel.navigateToSpam();
        } else if (id == R.id.nav_example) {
            // This should not happen anymore as we remove the example
            viewModel.navigateToExample();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            handleLogout();
        } else {
            // Handle dynamically created label items
            if (userLabels != null) {
                String itemTitle = item.getTitle().toString();
                for (Label label : userLabels) {
                    if (label.getName().equals(itemTitle)) {
                        viewModel.navigateToLabel(label.getName());
                        break;
                    }
                }
            }
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLogout() {
        // Clear the token and navigate back to login
        viewModel.logout();
        
        // Navigate to login activity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_MAIL_DETAIL && resultCode == RESULT_OK) {
            // Mail was deleted or updated, refresh the mail list
            viewModel.loadUserLabels(); // Refresh labels if needed
            // The ViewModel will automatically refresh the mails based on current filter
        }
    }


} 