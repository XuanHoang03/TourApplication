package com.hashmal.tourapplication.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.StatisticDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private TextView tvTotalUsers, tvTotalStaff, tvTotalTours, tvTotalBookings;
    private RecyclerView rvRecentActivities;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalStaff = view.findViewById(R.id.tvTotalStaff);
        tvTotalTours = view.findViewById(R.id.tvTotalTours);
        tvTotalBookings = view.findViewById(R.id.tvTotalBookings);
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);

        // Setup RecyclerView
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Load dashboard data
        loadDashboardData();
    }

    private void loadDashboardData() {
        apiService.getStatistics().enqueue(new Callback<StatisticDTO>() {
            @Override
            public void onResponse(Call<StatisticDTO> call, Response<StatisticDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatisticDTO stats = response.body();
                    tvTotalUsers.setText(String.valueOf(stats.getTotalUsers()));
                    tvTotalStaff.setText(String.valueOf(stats.getTotalStaffs()));
                    tvTotalTours.setText(String.valueOf(stats.getTotalTourPrograms()));
                    tvTotalBookings.setText(String.valueOf(stats.getTotalBookings()));
                } else {
                    tvTotalUsers.setText("-");
                    tvTotalStaff.setText("-");
                    tvTotalTours.setText("-");
                    tvTotalBookings.setText("-");
                }
            }

            @Override
            public void onFailure(Call<StatisticDTO> call, Throwable t) {
                tvTotalUsers.setText("-");
                tvTotalStaff.setText("-");
                tvTotalTours.setText("-");
                tvTotalBookings.setText("-");
            }
        });
    }
} 