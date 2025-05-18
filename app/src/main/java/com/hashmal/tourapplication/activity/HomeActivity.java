package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.TourAdapter;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements TourAdapter.OnTourClickListener {
    private RecyclerView recyclerView;
    private TourAdapter tourAdapter;
    private List<TourResponseDTO> tourList;
    private Map<String, TourResponseDTO>  tourMap;
    private ImageView userAvatar;
    private TextView userName;
    private ImageView notificationBell;
    private ApiService apiService;

    private LocalDataService localDataService;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        recyclerView = findViewById(R.id.toursRecyclerView);
        userAvatar = findViewById(R.id.userAvatar);
        userName = findViewById(R.id.userName);
        notificationBell = findViewById(R.id.notificationBell);

        // Setup RecyclerView
        tourList = new ArrayList<>();
        tourMap = new HashMap<>();
        tourAdapter = new TourAdapter(this, tourList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tourAdapter);

        // LoadService
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);

        // Load user data
        UserDTO userDTO = localDataService.getCurrentUser();

        userName.setText(userDTO.getProfile().getFullName());
        Glide.with(this)
                .load(userDTO.getProfile().getAvatarUrl())
                .error(R.drawable.default_avatar)
                .circleCrop()
                .into(userAvatar);

        // Load tours
        loadTours();

        // Setup notification bell click listener
        notificationBell.setOnClickListener(v -> {
            // TODO: Handle notification click
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadTours() {
        apiService.getAllTours().enqueue(new Callback<List<TourResponseDTO>>() {
            @Override
            public void onResponse(Call<List<TourResponseDTO>> call, Response<List<TourResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tourList.clear();
                    tourList.addAll(response.body());
                    tourMap = new HashMap<>();
                    for (TourResponseDTO tour : response.body()) {
                        tourMap.put(tour.getTourId(), tour);
                    }

                    tourAdapter.updateTours(tourList);
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to load tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TourResponseDTO>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTourClick(TourResponseDTO tour) {
        // Navigate to TourDetailActivity when a tour is clicked
        Intent intent = new Intent(HomeActivity.this, TourDetailActivity.class);
        String tourJson = gson.toJson(tour);
        intent.putExtra("tour", tourJson);
        startActivity(intent);
    }
}