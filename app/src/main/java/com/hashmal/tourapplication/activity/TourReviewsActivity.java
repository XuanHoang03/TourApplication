package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.ReviewAdapter;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.ReviewDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourReviewsActivity extends AppCompatActivity {
    private TextView tvTourName;
    private View layoutEmpty;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private ApiService apiService;
    private String tourId, tourName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_reviews);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Đánh giá");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        tvTourName = findViewById(R.id.tvTourName);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        rvReviews = findViewById(R.id.rvReviews);
        
        // Setup RecyclerView
        setupRecyclerView();

        // Get data from intent
        tourId = getIntent().getStringExtra("tourId");
        tourName = getIntent().getStringExtra("tourName");

        if (tourName != null) {
            tvTourName.setText(tourName);
        }

        apiService = ApiClient.getApiService();

        // Load reviews
        if (tourId != null) {
            loadReviews(tourId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin tour", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadReviews(String tourId) {
        apiService.getReviewsByTourId(tourId).enqueue(new Callback<List<ReviewDTO>>() {
            @Override
            public void onResponse(Call<List<ReviewDTO>> call, Response<List<ReviewDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<ReviewDTO> reviews = response.body();
                    displayReviews(reviews);
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<ReviewDTO>> call, Throwable t) {
                Toast.makeText(TourReviewsActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }
    
    private void displayReviews(List<ReviewDTO> reviews) {
        rvReviews.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        
        reviewAdapter.updateReviews(reviews);
    }

    private void showEmptyState() {
        rvReviews.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
    
    public void refreshReviews() {
        if (tourId != null) {
            loadReviews(tourId);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh reviews khi quay lại màn hình
        if (tourId != null) {
            loadReviews(tourId);
        }
    }
} 