package com.hashmal.tourapplication.fragment;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.ChangePasswordActivity;
import com.hashmal.tourapplication.activity.LoginActivity;
import com.hashmal.tourapplication.activity.ProfileActivity;
import com.hashmal.tourapplication.activity.TourHistoryActivity;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private Button logoutButton;
    private LinearLayout profileTab, bookingHistoryTab, changePasswordTab;
    private LocalDataService localDataService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        localDataService = LocalDataService.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);



        // Initialize views
        profileTab = view.findViewById(R.id.profileTab);
        profileTab.setOnClickListener(v -> openProfileActivity());

        bookingHistoryTab = view.findViewById(R.id.bookingHistoryTab);
        bookingHistoryTab.setOnClickListener(v -> openTourHistoryActivity());

        changePasswordTab = view.findViewById(R.id.changePasswordTab);
        changePasswordTab.setOnClickListener(v -> openChangePasswordActivity());

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        if (localDataService.getCurrentUser().getAccount().getAccountId().equals(RoleEnum.GUEST.name())) {
            profileTab.setVisibility(GONE);
            bookingHistoryTab.setVisibility(GONE);
            changePasswordTab.setVisibility(GONE);
        }
        return view;
    }

    private void openChangePasswordActivity() {
        Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(requireContext(), ProfileActivity.class);
        startActivity(intent);
    }

    private void openTourHistoryActivity() {
        Intent intent = new Intent(requireContext(), TourHistoryActivity.class);
        startActivity(intent);
    }


    private void logout() {
        // Clear local data
        localDataService.clearUserData();
        
        // Navigate to login screen
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}