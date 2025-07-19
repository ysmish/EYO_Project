package com.example.eyo.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import com.example.eyo.R;
import com.example.eyo.data.Label;
import com.example.eyo.ui.auth.LoginActivity;
import com.example.eyo.ui.labels.CreateLabelActivity;
import com.example.eyo.viewmodel.HomeViewModel;

import java.util.List;

public class NavigationHandler implements NavigationView.OnNavigationItemSelectedListener {
    
    private final Context context;
    private final HomeViewModel viewModel;
    private final NavigationView navigationView;
    private final DrawerLayout drawerLayout;
    private List<Label> userLabels;
    
    public NavigationHandler(Context context, HomeViewModel viewModel, NavigationView navigationView, DrawerLayout drawerLayout) {
        this.context = context;
        this.viewModel = viewModel;
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
    }
    
    /**
     * Populates the navigation menu with user labels
     * @param labels List of user labels to add to the menu
     */
    public void populateLabelsInNavigationMenu(List<Label> labels) {
        // Store labels for use in navigation item selection
        this.userLabels = labels;
        
        Menu menu = navigationView.getMenu();
        
        // Find the Labels submenu by title
        SubMenu labelsSubmenu = null;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu() && "Labels".equals(item.getTitle().toString())) {
                labelsSubmenu = item.getSubMenu();
                break;
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
                Drawable iconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_label);
                if (iconDrawable != null) {
                    iconDrawable = DrawableCompat.wrap(iconDrawable).mutate();
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
            
            // Add "Create New Label" button at the end
            MenuItem createLabelItem = labelsSubmenu.add(Menu.NONE, R.id.nav_create_label, Menu.NONE, "Create new");
            Drawable addIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_add);
            if (addIconDrawable != null) {
                addIconDrawable = DrawableCompat.wrap(addIconDrawable).mutate();
                int tintColor = ContextCompat.getColor(context, R.color.navigation_icon_tint);
                DrawableCompat.setTint(addIconDrawable, tintColor);
                createLabelItem.setIcon(addIconDrawable);
            }
            createLabelItem.setCheckable(false);
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
        } else if (id == R.id.nav_create_label) {
            handleCreateNewLabel();
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
        
        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    /**
     * Handles the logout functionality
     */
    private void handleLogout() {
        // Clear the token and navigate back to login
        viewModel.logout();
        
        // Navigate to login activity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        
        // Finish the current activity if it's an Activity
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).finish();
        }
    }
    
    /**
     * Handles the create new label functionality
     */
    private void handleCreateNewLabel() {
        Intent intent = new Intent(context, CreateLabelActivity.class);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, 100); // REQUEST_CODE_CREATE_LABEL = 100
        } else {
            context.startActivity(intent);
        }
    }
} 