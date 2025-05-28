package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.fragment.staff.StaffDashboardFragment;

public class StaffMainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new StaffDashboardFragment());
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_dashboard) {
            fragment = new StaffDashboardFragment();
        } else if (itemId == R.id.navigation_tours) {
//            fragment = new StaffToursFragment();
        } else if (itemId == R.id.navigation_bookings) {
//            fragment = new StaffBookingsFragment();
        } else if (itemId == R.id.navigation_profile) {
//            fragment = new StaffProfileFragment();
        }

        return loadFragment(fragment);
    }
} 