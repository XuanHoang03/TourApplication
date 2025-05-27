package com.hashmal.tourapplication.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.admin.AdminUsersAdapter;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private FloatingActionButton fabAddUser;
    private AdminUsersAdapter usersAdapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvUsers = view.findViewById(R.id.rvUsers);
        fabAddUser = view.findViewById(R.id.fabAddUser);

        // Setup RecyclerView
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        usersAdapter = new AdminUsersAdapter(requireContext());
        rvUsers.setAdapter(usersAdapter);

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Setup click listeners
        fabAddUser.setOnClickListener(v -> {
            // TODO: Show add user dialog
            Toast.makeText(requireContext(), "Add user clicked", Toast.LENGTH_SHORT).show();
        });

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        // TODO: Implement API call to get users
//        apiService.getUsers().enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // TODO: Parse and display users
//                } else {
//                    Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
} 