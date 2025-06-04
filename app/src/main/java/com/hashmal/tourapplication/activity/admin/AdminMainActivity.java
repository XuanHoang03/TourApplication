package com.hashmal.tourapplication.activity.admin;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.LoginActivity;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.fragment.admin.AdminDashboardFragment;
import com.hashmal.tourapplication.fragment.admin.AdminStaffFragment;
import com.hashmal.tourapplication.fragment.admin.AdminToursFragment;
import com.hashmal.tourapplication.fragment.admin.AdminUsersFragment;
import com.hashmal.tourapplication.model.UserRole;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.hashmal.tourapplication.utils.PermissionManager;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private LocalDataService localDataService;
    private PermissionManager permissionManager;

    private SysUserDTO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Initialize services
        localDataService = LocalDataService.getInstance(this);
        permissionManager = PermissionManager.getInstance();

        currentUser = localDataService.getSysUser();

        // Set user role based on login data

        permissionManager.setCurrentUserRole(RoleEnum.valueOf(currentUser.getAccount().getRoleName()));

        // Setup toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Setup drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update navigation header
        updateNavigationHeader();

        // Setup navigation menu based on permissions
        setupNavigationMenu();

        // Set default fragment
        if (savedInstanceState == null) {
            loadDefaultFragment();
        }
    }

    private void setupNavigationMenu() {
        // Show/hide menu items based on permissions
        navigationView.getMenu().findItem(R.id.nav_dashboard).setVisible(permissionManager.canViewDashboard());
        navigationView.getMenu().findItem(R.id.nav_users).setVisible(permissionManager.canManageUsers());
        navigationView.getMenu().findItem(R.id.nav_staff).setVisible(permissionManager.canManageStaff());
        navigationView.getMenu().findItem(R.id.nav_tours).setVisible(permissionManager.canManageTours());
        navigationView.getMenu().findItem(R.id.nav_bookings).setVisible(permissionManager.canManageBookings());
        navigationView.getMenu().findItem(R.id.nav_profile).setVisible(permissionManager.canViewProfile());
    }

    private void loadDefaultFragment() {
        Fragment fragment = null;
        if (permissionManager.canViewDashboard()) {
            toolbar.setTitle("Dashboard");
            fragment = new AdminDashboardFragment();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        } else if (permissionManager.canManageTours()) {
            fragment = new AdminToursFragment();
            navigationView.setCheckedItem(R.id.nav_tours);
        } else if (permissionManager.canViewProfile()) {
            // TODO: Load profile fragment
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        if (fragment != null) {
            loadFragment(fragment);
        }
    }

    private void updateNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvAdminEmail = headerView.findViewById(R.id.tvAdminEmail);
        TextView tvName = headerView.findViewById(R.id.tvName);

        tvAdminEmail.setText(currentUser.getProfile().getEmail());
        tvName.setText(currentUser.getProfile().getFullName());
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment fragment = null;

        if (itemId == R.id.nav_dashboard && permissionManager.canViewDashboard()) {
            toolbar.setTitle("Dashboard");
            fragment = new AdminDashboardFragment();
        } else if (itemId == R.id.nav_users && permissionManager.canManageUsers()) {
            toolbar.setTitle("User Management");
            fragment = new AdminUsersFragment();
        } else if (itemId == R.id.nav_staff && permissionManager.canManageStaff()) {
            toolbar.setTitle("Staff Management");
            fragment = new AdminStaffFragment();
        } else if (itemId == R.id.nav_tours && permissionManager.canManageTours()) {
            toolbar.setTitle("Tour Program Management");
            fragment = new AdminToursFragment();
        } else if (itemId == R.id.nav_bookings && permissionManager.canManageBookings()) {
            // TODO: Load bookings fragment
        } else if (itemId == R.id.nav_profile && permissionManager.canViewProfile()) {
            // TODO: Load profile fragment
        } else if (itemId == R.id.nav_logout) {
            handleLogout();
            return true;
        }

        if (fragment != null) {
            loadFragment(fragment);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    private void handleLogout() {
        // Clear user data
        localDataService.clearUserData();
        
        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 