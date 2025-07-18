package com.hashmal.tourapplication.fragment.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.TourGuideDetailActivity;
import com.hashmal.tourapplication.adapter.admin.AdminSysUsersAdapter;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.hashmal.tourapplication.service.dto.UserManagementDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourGuideViewActivity extends Fragment {
    private RecyclerView rvStaffs;
    private AdminSysUsersAdapter staffsAdapter;
    private ApiService apiService;
    private List<SysUserDTO> allStaffs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tour_guide_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvStaffs = view.findViewById(R.id.rvStaffs);
        SearchView searchView = view.findViewById(R.id.searchViewStaff);
        rvStaffs.setLayoutManager(new LinearLayoutManager(requireContext()));
        staffsAdapter = new AdminSysUsersAdapter(requireContext());
        staffsAdapter.setShowEditDeleteButtons(false);
        rvStaffs.setAdapter(staffsAdapter);
        apiService = ApiClient.getApiService();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterStaffs(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterStaffs(newText);
                return true;
            }
        });
        staffsAdapter.setOnSysUserClickListener(this::showTourGuideInfo);
        staffsAdapter.setOnSysUserActionListener(new AdminSysUsersAdapter.OnSysUserActionListener() {
            @Override
            public void onEditSysUser(SysUserDTO user) {
//                showEditStaffDialog(user);
            }
            @Override
            public void onDeleteSysUser(SysUserDTO user) {
//                showDeleteStaffDialog(user);
            }
        });
        loadStaffs();
    }

    private void showTourGuideInfo(SysUserDTO sysUserDTO) {
        Intent tourGuideInfo = new Intent(requireContext(), TourGuideDetailActivity.class);
        tourGuideInfo.putExtra("staffId", sysUserDTO.getAccount().getAccountId());
        startActivity(tourGuideInfo);
    }

    private void loadStaffs() {
        apiService.getTourGuide(1).enqueue(new Callback<List<SysUserDTO>>() {
            @Override
            public void onResponse(Call<List<SysUserDTO>> call, Response<List<SysUserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                        String json = new Gson().toJson(response.body());
                        Type userType = new TypeToken<List<SysUserDTO>>() {}.getType();
                        List<SysUserDTO> data = new Gson().fromJson(json, userType);
                        if (data.isEmpty()) {
//                            TODO: if dont have any tour guide yet
                        }
                        allStaffs = data;
                        staffsAdapter.updateSysUsers(allStaffs);
                } else {
                    Toast.makeText(requireContext(), "Failed to load staffs", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<SysUserDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterStaffs(String query) {
        if (allStaffs == null) return;
        String lower = query.toLowerCase();
        List<SysUserDTO> filtered = new ArrayList<>();
        for (SysUserDTO staff : allStaffs) {
            if (staff.getProfile().getFullName().toLowerCase().contains(lower)
                    || staff.getProfile().getEmail().toLowerCase().contains(lower)
                    || staff.getProfile().getPhoneNumber().toLowerCase().contains(lower)
                    || staff.getAccount().getUsername().toLowerCase().contains(lower)
                    || staff.getAccount().getAccountId().toLowerCase().contains(lower)) {
                filtered.add(staff);
            }
        }
        staffsAdapter.updateSysUsers(filtered);
    }
}