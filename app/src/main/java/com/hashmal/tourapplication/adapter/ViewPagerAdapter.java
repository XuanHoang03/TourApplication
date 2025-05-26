package com.hashmal.tourapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hashmal.tourapplication.fragment.AccountFragment;
import com.hashmal.tourapplication.fragment.CalendarFragment;
import com.hashmal.tourapplication.fragment.HomeFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private HomeFragment homeFragment;
    private CalendarFragment calendarFragment;
    private AccountFragment accountFragment;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        homeFragment = new HomeFragment();
        calendarFragment = new CalendarFragment();
        accountFragment = new AccountFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return homeFragment;
            case 1:
                return calendarFragment;
            case 2:
                return accountFragment;
            default:
                return homeFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public CalendarFragment getCalendarFragment() {
        return calendarFragment;
    }

    public AccountFragment getAccountFragment() {
        return accountFragment;
    }
} 