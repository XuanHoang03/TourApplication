package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.dialog.CustomDialog;
import com.hashmal.tourapplication.service.LocalDataService;

public class AccountActivity extends AppCompatActivity {
    private LinearLayout profileTab;
    private LinearLayout bookingHistoryTab;
    private LinearLayout settingsTab;
    private LinearLayout helpTab;
    private Button logoutButton;
    private LocalDataService localDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize LocalDataService
        localDataService = LocalDataService.getInstance(this);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        profileTab = findViewById(R.id.profileTab);
        bookingHistoryTab = findViewById(R.id.bookingHistoryTab);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupClickListeners() {
        // Profile Tab Click
        profileTab.setOnClickListener(v -> {
            // TODO: Navigate to profile screen
            Toast.makeText(this, "Chuyển đến trang thông tin cá nhân", Toast.LENGTH_SHORT).show();
        });

        // Booking History Tab Click
        bookingHistoryTab.setOnClickListener(v -> {
            // TODO: Navigate to booking history screen
            Toast.makeText(this, "Chuyển đến trang lịch sử đặt tour", Toast.LENGTH_SHORT).show();
        });



        logoutButton.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void showLogoutConfirmationDialog() {
        new CustomDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Do you want to log out ?")
                .setIcon(getDrawable(R.drawable.ic_info))
                .setPositiveButton("Yes", dialog -> {
                    // Clear user data
                    localDataService.clearUserData();
                    // Navigate to login screen
                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                })
                .setSingleButtonMode(false)
                .show();
    }
}