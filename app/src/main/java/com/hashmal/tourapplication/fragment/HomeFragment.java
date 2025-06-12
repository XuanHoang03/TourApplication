package com.hashmal.tourapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.HomeActivity;
import com.hashmal.tourapplication.activity.TourDetailActivity;
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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements TourAdapter.OnTourClickListener {
    private RecyclerView recyclerView;
    private TourAdapter tourAdapter;
    private List<TourResponseDTO> tourList;
    private List<TourResponseDTO> filteredTourList;
    private Map<String, TourResponseDTO> tourMap;
    private ImageView userAvatar;
    private TextView userName;
    private ImageView notificationBell;
    private SearchView searchView;
    private ApiService apiService;
    private LocalDataService localDataService;
    private Gson gson = new Gson();
    private String fullName = "Guest";
    private String avatarUrl = null;
    private boolean isDataLoaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        tourList = new ArrayList<>();
        filteredTourList = new ArrayList<>();
        apiService = ApiClient.getApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.toursRecyclerView);
        userAvatar = view.findViewById(R.id.userAvatar);
        userName = view.findViewById(R.id.userName);
        notificationBell = view.findViewById(R.id.notificationBell);
        searchView = view.findViewById(R.id.searchView);

        // Setup RecyclerView
        tourAdapter = new TourAdapter(requireContext(), filteredTourList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(tourAdapter);

        // Setup SearchView
        setupSearchView();

        // LoadService
        localDataService = LocalDataService.getInstance(requireContext());

        // Load user data
        UserDTO userDTO = localDataService.getCurrentUser();
        if (userDTO != null) {
            fullName = userDTO.getProfile().getFullName();
            avatarUrl = userDTO.getProfile().getAvatarUrl();
        }
        userName.setText(fullName);
        Glide.with(this)
                .load(avatarUrl)
                .error(R.drawable.default_avatar)
                .circleCrop()
                .into(userAvatar);

        if (!isDataLoaded) {
            loadTours();
        }

        // Setup notification bell click listener
        notificationBell.setOnClickListener(v -> {
            // TODO: Handle notification click
            Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTours(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTours(newText);
                return true;
            }
        });
    }

    private void filterTours(String query) {
        if (query == null || query.isEmpty()) {
            filteredTourList.clear();
            filteredTourList.addAll(tourList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredTourList = tourList.stream()
                    .filter(tour -> 
                        tour.getTourName().toLowerCase().contains(lowerCaseQuery) ||
                        tour.getTourType().toLowerCase().contains(lowerCaseQuery) ||
                        tour.getTourDescription().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }
        tourAdapter.updateTours(filteredTourList);
    }

    private void loadTours() {
        apiService.getAllToursForUser(localDataService.getCurrentUser().getAccount().getAccountId()).enqueue(new Callback<List<TourResponseDTO>>() {
            @Override
            public void onResponse(Call<List<TourResponseDTO>> call, Response<List<TourResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tourList.clear();
                    tourList.addAll(response.body());
                    filteredTourList.clear();
                    filteredTourList.addAll(tourList);
                    tourMap = new HashMap<>();
                    for (TourResponseDTO tour : response.body()) {
                        tourMap.put(tour.getTourId(), gson.fromJson(gson.toJson(tour), TourResponseDTO.class));
                    }
                    tourAdapter.updateTours(filteredTourList);
                    isDataLoaded = true;
                } else {
                    Toast.makeText(requireContext(), "Failed to load tours", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TourResponseDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshData() {
        isDataLoaded = false;
        loadTours();
    }

    @Override
    public void onTourClick(TourResponseDTO tour) {
        // Navigate to TourDetailActivity when a tour is clicked
        Intent intent = new Intent(requireContext(), TourDetailActivity.class);
        String tourJson = gson.toJson(tour);
        intent.putExtra("tour", tourJson);
        startActivity(intent);
    }
} 