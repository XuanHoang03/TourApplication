package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.ViewPagerAdapter;
import com.hashmal.tourapplication.fragment.AccountFragment;
import com.hashmal.tourapplication.fragment.CalendarFragment;
import com.hashmal.tourapplication.fragment.HomeFragment;
import com.hashmal.tourapplication.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Disable ViewPager2 swipe
        viewPager.setUserInputEnabled(false);

        // Setup ViewPager2
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Setup BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (itemId == R.id.navigation_calendar) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (itemId == R.id.navigation_account) {
                    viewPager.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });

        // Sync ViewPager2 with BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_calendar);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_account);
                        break;
                }
            }
        });
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public HomeFragment getHomeFragment() {
        return viewPagerAdapter.getHomeFragment();
    }

    public CalendarFragment getCalendarFragment() {
        return viewPagerAdapter.getCalendarFragment();
    }

    public AccountFragment getProfileFragment() {
        return viewPagerAdapter.getAccountFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instance == this) {
            instance = null;
        }
    }
} 