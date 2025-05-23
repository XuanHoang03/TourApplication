package com.hashmal.tourapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hashmal.tourapplication.fragment.AccountFragment;
import com.hashmal.tourapplication.fragment.HomeFragment;
import com.hashmal.tourapplication.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;
    private HomeFragment homeFragment;
    private AccountFragment accountFragment;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        homeFragment = new HomeFragment();
        accountFragment = new AccountFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return homeFragment;
            case 1:
                return accountFragment;
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

    public AccountFragment getAccountFragment() {
        return accountFragment;
    }
} 