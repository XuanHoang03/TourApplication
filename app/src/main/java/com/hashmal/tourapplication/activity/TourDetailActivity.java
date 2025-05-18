package com.hashmal.tourapplication.activity;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.util.Log;
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
import com.hashmal.tourapplication.model.TourPackage;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class TourDetailActivity extends AppCompatActivity implements TourPackageAdapter.OnPackageClickListener {
    private ImageView tourImage;
    private TextView tourName, tourDescription, tourDuration;
    private RatingBar tourRating;
    private RecyclerView packagesRecyclerView;
    private TourPackageAdapter packageAdapter;
    private List<TourPackageDTO> packages = new ArrayList<>();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        // Initialize views
        initializeViews();
        
        // Get tour data from intent
        String tourJson = getIntent().getStringExtra("tour");
        TourResponseDTO tour = gson.fromJson(tourJson, TourResponseDTO.class);
        if (tour != null) {
            displayTourDetails(tour);
            packages = tour.getPackages();
            // Setup packages RecyclerView
        setupPackagesRecyclerView(packages);
        }
    }

    private void initializeViews() {
        tourImage = findViewById(R.id.tourImage);
        tourName = findViewById(R.id.tourName);
        tourDescription = findViewById(R.id.tourDescription);
        tourDuration = findViewById(R.id.tourDuration);
        tourRating = findViewById(R.id.tourRating);
        packagesRecyclerView = findViewById(R.id.packagesRecyclerView);
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
        loadLocationImages(tour.getLocations());
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
                    if (tourPackage != null) {
                        Toast.makeText(TourDetailActivity.this, 
                            "Selected package: " + tourPackage.getPackageName(), 
                            Toast.LENGTH_SHORT).show();
                    }
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
    public void onPackageClick(TourPackageDTO tourPackage) {

    }
}