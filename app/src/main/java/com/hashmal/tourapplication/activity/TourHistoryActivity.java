package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.BookingHistoryAdapter;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.DisplayBookingDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourHistoryActivity extends AppCompatActivity implements BookingHistoryAdapter.OnBookingClickListener {
    private RecyclerView recyclerView;
    private ApiService apiService;
    private LocalDataService localDataService;
    private ProgressBar progressBar;
    private View emptyStateView;
    private Toolbar toolbar;

    private List<DisplayBookingDTO> bookingHistory = new ArrayList<>();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_history);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateView = findViewById(R.id.emptyStateView);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize services
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);

        // Load booking history
        loadBookingHistory();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadBookingHistory() {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.GONE);

        // Get current user ID
        String userId = localDataService.getCurrentUser().getAccount().getAccountId();
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API to get booking history
        apiService.getBookingsByUserId(userId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse resp = response.body();
                    String listBookings = gson.toJson(resp.getData());
                    Type bookingType = new TypeToken<List<DisplayBookingDTO>>() {}.getType();
                    bookingHistory = gson.fromJson(listBookings, bookingType);

                    if (bookingHistory != null && !bookingHistory.isEmpty()) {
                        // Show booking history
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateView.setVisibility(View.GONE);

                        // Set up adapter with booking history data
                        BookingHistoryAdapter adapter = new BookingHistoryAdapter(bookingHistory, TourHistoryActivity.this, TourHistoryActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Show empty state
                        recyclerView.setVisibility(View.GONE);
                        emptyStateView.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Handle error response
                    Toast.makeText(TourHistoryActivity.this, "Failed to load booking history", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                    emptyStateView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyStateView.setVisibility(View.VISIBLE);
                Toast.makeText(TourHistoryActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBookingClick(DisplayBookingDTO tour) {
        Intent intent = new Intent(TourHistoryActivity.this, YourTourActivity.class);
        intent.putExtra("bookingId", tour.getBookingId());
        startActivity(intent);
    }
}