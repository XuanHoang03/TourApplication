package com.hashmal.tourapplication.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.bumptech.glide.Glide;
import com.hashmal.tourapplication.service.dto.Profile;
import com.hashmal.tourapplication.service.dto.SystemAccount;
import com.hashmal.tourapplication.service.dto.TourGuideScheduleDTO;
import com.hashmal.tourapplication.adapter.TourGuideScheduleAdapter;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourGuideDetailActivity extends AppCompatActivity {
    private ApiService apiService;
    private LocalDataService localDataService;
    private ImageView imgAvatar;
    private TextView tvFullName, tvEmail, tvPhone, tvGender, tvDob, tvAddress, tvStatus;
    private ProgressBar progressBar;
    private Gson gson = new Gson();
    private RecyclerView rvTourSchedule;
    private TourGuideScheduleAdapter scheduleAdapter;
    private List<TourGuideScheduleDTO> scheduleList = new ArrayList<>();
    private TextView tvNoTourSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_tour_guide_detail);

        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        tvDob = findViewById(R.id.tvDob);
        tvAddress = findViewById(R.id.tvAddress);
        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(getResources().getIdentifier("progressBar", "id", getPackageName()));
        rvTourSchedule = findViewById(R.id.rvTourSchedule);
        tvNoTourSchedule = findViewById(R.id.tvNoTourSchedule);

        // Xử lý nút back
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        Intent thisIntent = getIntent();
        String staffId = thisIntent.getStringExtra("staffId");
        String userJson = thisIntent.getStringExtra("tourGuide");
        if (staffId != null) {
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            apiService.getStaffInfo(staffId).enqueue(new Callback<SysUserDTO>() {
                @Override
                public void onResponse(Call<SysUserDTO> call, Response<SysUserDTO> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        bindData(response.body());
                    } else {
                        Toast.makeText(TourGuideDetailActivity.this, "Không lấy được thông tin hướng dẫn viên", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<SysUserDTO> call, Throwable t) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(TourGuideDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else if (userJson != null) {
            SysUserDTO user = gson.fromJson(userJson, SysUserDTO.class);
            bindData(user);
        } else {
            Toast.makeText(this, "Không có dữ liệu hướng dẫn viên", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void bindData(SysUserDTO user) {
        Profile profile = user.getProfile();
        SystemAccount account = user.getAccount();
        // Avatar
        String avatarUrl = profile != null ? profile.getAvatarUrl() : null;
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .circleCrop()
                .into(imgAvatar);
        // Họ tên
        tvFullName.setText(profile != null && profile.getFullName() != null ? profile.getFullName() : "Chưa cập nhật");
        // Email
        tvEmail.setText(profile != null && profile.getEmail() != null ? profile.getEmail() : "Chưa cập nhật");
        // Số điện thoại
        tvPhone.setText(profile != null && profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "Chưa cập nhật");
        // Giới tính
        String genderStr = "Chưa cập nhật";
        if (profile != null && profile.getGender() != null) {
            switch (profile.getGender()) {
                case 1:
                    genderStr = "Nam";
                    break;
                case 0:
                    genderStr = "Nữ";
                    break;
                default:
                    genderStr = "Khác";
                    break;
            }
        }
        tvGender.setText(genderStr);
        // Ngày sinh
        tvDob.setText(profile != null && profile.getDob() != null ? profile.getDob() : "Chưa cập nhật");
        // Địa chỉ
        String address = "";
        if (profile != null) {
            if (profile.getAddress() != null) address += profile.getAddress();
            if (profile.getDistrict() != null) address += ", " + profile.getDistrict();
            if (profile.getCity() != null) address += ", " + profile.getCity();
            if (profile.getProvince() != null) address += ", " + profile.getProvince();
        }
        tvAddress.setText(address.isEmpty() ? "Chưa cập nhật" : address);
        // Trạng thái tài khoản
        String statusStr = "Chưa cập nhật";
        if (account != null && account.getStatus() != null) {
            switch (account.getStatus()) {
                case 1: {
                    statusStr = "Đang hoạt động";
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                }
                case 0:
                    statusStr = "Đã vô hiệu hóa";
                    tvStatus.setTextColor(Color.parseColor("#8B0000"));
                    break;
                default:
                    statusStr = "Không xác định";
                    tvStatus.setTextColor(Color.parseColor("#757575"));
                    break;
            }
        }
        tvStatus.setText(statusStr);

        // Load lịch dẫn tour
        String tourGuideId = null;
        if (account != null && account.getAccountId() != null) {
            tourGuideId = account.getAccountId();
        } else if (profile != null && profile.getProfileId() != null) {
            tourGuideId = profile.getProfileId();
        }
        if (tourGuideId != null) {
            apiService.getTourGuideSchedules(tourGuideId).enqueue(new Callback<List<TourGuideScheduleDTO>>() {
                @Override
                public void onResponse(Call<List<TourGuideScheduleDTO>> call, Response<List<TourGuideScheduleDTO>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        scheduleList.clear();
                        scheduleList.addAll(response.body());
                        scheduleAdapter.notifyDataSetChanged();
                        tvNoTourSchedule.setVisibility(View.GONE);
                        rvTourSchedule.setVisibility(View.VISIBLE);
                    } else {
                        scheduleList.clear();
                        scheduleAdapter.notifyDataSetChanged();
                        tvNoTourSchedule.setVisibility(View.VISIBLE);
                        rvTourSchedule.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<List<TourGuideScheduleDTO>> call, Throwable t) {
                    Toast.makeText(TourGuideDetailActivity.this, "Lỗi khi tải lịch dẫn tour: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    tvNoTourSchedule.setVisibility(View.VISIBLE);
                    rvTourSchedule.setVisibility(View.GONE);
                }
            });
        }

        scheduleAdapter = new TourGuideScheduleAdapter(scheduleList);
        rvTourSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvTourSchedule.setAdapter(scheduleAdapter);
    }
}