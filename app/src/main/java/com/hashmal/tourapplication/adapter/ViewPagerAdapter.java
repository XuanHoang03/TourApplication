package com.hashmal.tourapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hashmal.tourapplication.fragment.HomeFragment;
import com.hashmal.tourapplication.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return homeFragment;
            case 1:
                return profileFragment;
            default:
                return homeFragment;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public ProfileFragment getProfileFragment() {
        return profileFragment;
    }
} 