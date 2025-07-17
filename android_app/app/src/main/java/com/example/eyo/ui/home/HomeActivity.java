package com.example.eyo.ui.home;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.R;
import com.example.eyo.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabCompose;
    private RecyclerView mailsRecyclerView;
    private View emptyState;
    private TextInputEditText searchEditText;
    
    private HomeViewModel viewModel;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupToolbar();
        setupNavigationDrawer();
        setupViewModel();
        setupClickListeners();
        observeViewModel();
        
        // Set default navigation item
        navigationView.setCheckedItem(R.id.nav_inbox);
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        fabCompose = findViewById(R.id.fab_compose);
        mailsRecyclerView = findViewById(R.id.mails_recycler_view);
        emptyState = findViewById(R.id.empty_state);
        searchEditText = findViewById(R.id.search_edit_text);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Inbox");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupNavigationDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // You can add code here to handle drawer closed
            }
            
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // You can add code here to handle drawer opened
            }
        };
        
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        
        navigationView.setNavigationItemSelectedListener(this);
        
        // Update navigation header with user info
        updateNavigationHeader();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void setupClickListeners() {
        fabCompose.setOnClickListener(v -> {
            viewModel.openCompose();
            Toast.makeText(this, "Compose clicked", Toast.LENGTH_SHORT).show();
        });
        
        // Search functionality
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            viewModel.performSearch(query);
            return true;
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentFilter().observe(this, filter -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(viewModel.getTitleForFilter(filter));
            }
            updateMailsList(filter);
        });
        
        viewModel.getIsLoading().observe(this, isLoading -> {
            // TODO: Show/hide loading indicator
            if (isLoading) {
                emptyState.setVisibility(View.GONE);
                mailsRecyclerView.setVisibility(View.GONE);
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getSearchQuery().observe(this, query -> {
            // Update search results
            updateMailsList(viewModel.getCurrentFilter().getValue());
        });
    }

    private void updateNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.nav_header_name);
        TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
        
        // TODO: Get actual user info from preferences or user repository
        nameTextView.setText("EYO Mail");
        emailTextView.setText("user@example.com");
    }

    private void updateMailsList(String filter) {
        // TODO: Implement actual mail loading based on filter
        // For now, just show empty state
        mailsRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_inbox) {
            viewModel.navigateToInbox();
        } else if (id == R.id.nav_sent) {
            viewModel.navigateToSent();
        } else if (id == R.id.nav_drafts) {
            viewModel.navigateToDrafts();
        } else if (id == R.id.nav_starred) {
            viewModel.navigateToStarred();
        } else if (id == R.id.nav_spam) {
            viewModel.navigateToSpam();
        } else if (id == R.id.nav_trash) {
            viewModel.navigateToTrash();
        } else if (id == R.id.nav_important) {
            viewModel.navigateToImportant();
        } else if (id == R.id.nav_personal) {
            viewModel.navigateToPersonal();
        } else if (id == R.id.nav_work) {
            viewModel.navigateToWork();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            handleLogout();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLogout() {
        // TODO: Implement logout functionality
        Toast.makeText(this, "Logout functionality will be implemented later", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
} 