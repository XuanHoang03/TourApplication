package com.hashmal.tourapplication.activity;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.TourPackageAdapter;
import com.hashmal.tourapplication.adapter.TourScheduleAdapter;
import com.hashmal.tourapplication.model.TourPackage;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TourDetailActivity extends AppCompatActivity implements TourPackageAdapter.OnPackageClickListener, TourScheduleAdapter.OnScheduleClickListener {
    private ImageView tourImage;
    private TextView tourName, tourDescription, tourDuration;
    private RatingBar tourRating;
    private RecyclerView packagesRecyclerView;
    private RecyclerView schedulesRecyclerView;
    private TourPackageAdapter packageAdapter;
    private TourScheduleAdapter scheduleAdapter;
    private List<TourPackageDTO> packages = new ArrayList<>();
    private List<TourScheduleResponseDTO> schedules = new ArrayList<>();
    private Gson gson = new Gson();
    private Button bookingButton;
    private TourPackageDTO selectedPackage;
    private TourScheduleResponseDTO selectedSchedule;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        // Initialize views
        initializeViews();
        
        // Get tour data from intent
        String tourJson = getIntent().getStringExtra("tour");
        TourResponseDTO tour = gson.fromJson(tourJson, TourResponseDTO.class);
        apiService = ApiClient.getApiService();
        if (tour != null) {
            displayTourDetails(tour);
            packages = tour.getPackages();
            // Setup packages RecyclerView
            setupPackagesRecyclerView(packages);
        }

        // Setup booking button
        bookingButton.setOnClickListener(v -> {
            if (selectedPackage == null) {
                Toast.makeText(this, "Vui lòng chọn gói tour", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSchedule == null) {
                Toast.makeText(this, "Vui lòng chọn lịch trình", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Navigate to booking screen
            Toast.makeText(this, "Đang xử lý đặt vé...", Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeViews() {
        tourImage = findViewById(R.id.tourImage);
        tourName = findViewById(R.id.tourName);
        tourDescription = findViewById(R.id.tourDescription);
        tourDuration = findViewById(R.id.tourDuration);
        tourRating = findViewById(R.id.tourRating);
        packagesRecyclerView = findViewById(R.id.packagesRecyclerView);
        schedulesRecyclerView = findViewById(R.id.schedulesRecyclerView);
        bookingButton = findViewById(R.id.bookingButton);
        
        // Setup schedules RecyclerView
        scheduleAdapter = new TourScheduleAdapter(this, schedules, this);
        schedulesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        schedulesRecyclerView.setAdapter(scheduleAdapter);
    }

    private void displayTourDetails(TourResponseDTO tour) {
        // Load tour image
        Glide.with(this)
            .load(tour.getThumbnailUrl())
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(tourImage);

        // Set tour information
        tourName.setText(tour.getTourName());
        tourDescription.setText(tour.getTourDescription());
        tourDuration.setText(tour.getDuration());
//        tourRating.setRating(tour.get());

        // Load location images
        loadTourSchedules(tour.getTourId());

        loadLocationImages(tour.getLocations());
    }

    private void loadTourSchedules(String tourId) {
        apiService.getTourSchedulesByTourId(tourId).enqueue(new Callback<List<TourScheduleResponseDTO>>() {
            @Override
            public void onResponse(Call<List<TourScheduleResponseDTO>> call, Response<List<TourScheduleResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TourScheduleResponseDTO> newSchedules = response.body();
                    schedules.clear();
                    schedules.addAll(newSchedules);
                    scheduleAdapter.updateSchedules(schedules);
                    Log.d("TourDetail", "Successfully loaded " + schedules.size() + " tour schedules");
                } else {
                    Log.e("TourDetail", "Failed to load tour schedules: " + response.code());
                    Toast.makeText(TourDetailActivity.this, 
                        "Failed to load tour schedules", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TourScheduleResponseDTO>> call, Throwable t) {
                Log.e("TourDetail", "Error loading tour schedules", t);
                Toast.makeText(TourDetailActivity.this, 
                    "Error: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocationImages(List<LocationDTO> locations) {
        // TODO: Implement loading location images into the HorizontalScrollView
        // This will be implemented when we have the actual location images data
    }

    private void setupPackagesRecyclerView(List<TourPackageDTO> tourPackages) {
        try {
            if (tourPackages == null) {
                tourPackages = new ArrayList<>();
                Log.d("TourDetail", "Tour packages list is null, initializing empty list");
            }

            packageAdapter = new TourPackageAdapter(this, tourPackages, new TourPackageAdapter.OnPackageClickListener() {
                @Override
                public void onPackageClick(TourPackageDTO tourPackage) {
                    selectedPackage = tourPackage;
                    Toast.makeText(TourDetailActivity.this, 
                        "Đã chọn gói: " + tourPackage.getPackageName(), 
                        Toast.LENGTH_SHORT).show();
                }
            });

            if (packagesRecyclerView != null) {
                packagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                packagesRecyclerView.setAdapter(packageAdapter);
                Log.d("TourDetail", "Successfully set up RecyclerView with " + tourPackages.size() + " packages");
            } else {
                Log.e("TourDetail", "RecyclerView is null");
            }
        } catch (Exception e) {
            Log.e("TourDetail", "Error setting up packages RecyclerView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onScheduleClick(TourScheduleResponseDTO schedule) {
        selectedSchedule = schedule;
        Toast.makeText(this, "Đã chọn lịch trình", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPackageClick(TourPackageDTO tourPackage) {

    }
}