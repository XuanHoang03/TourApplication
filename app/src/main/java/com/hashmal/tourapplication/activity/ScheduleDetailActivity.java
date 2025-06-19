package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.LinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Button;

public class ScheduleDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chi tiết lịch trình");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String json = getIntent().getStringExtra("schedule");
        TourScheduleResponseDTO schedule = new Gson().fromJson(json, TourScheduleResponseDTO.class);

        TextView tvName = findViewById(R.id.tvScheduleName);
        TextView tvDateRange = findViewById(R.id.tvDateRange);
        TextView tvTicketCount = findViewById(R.id.tvTicketCount);
        TextView tvStatus = findViewById(R.id.tvStatus);

        tvName.setText(schedule.getTourScheduleId() != null ? schedule.getTourScheduleId() : "Lịch trình tour");
        // Ngày bắt đầu - kết thúc
        try {
            String start = schedule.getStartTime();
            String end = schedule.getEndTime();
            LocalDateTime startDate, endDate;
            DateTimeFormatter inputFmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            DateTimeFormatter inputFmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (start.length() > 19) {
                startDate = LocalDateTime.parse(start, inputFmt1);
            } else {
                startDate = LocalDateTime.parse(start, inputFmt2);
            }
            if (end != null && end.length() > 19) {
                endDate = LocalDateTime.parse(end, inputFmt1);
            } else if (end != null) {
                endDate = LocalDateTime.parse(end, inputFmt2);
            } else {
                endDate = startDate;
            }
            String text = outFmt.format(startDate);
            if (!startDate.toLocalDate().equals(endDate.toLocalDate())) {
                text += " - " + outFmt.format(endDate);
            }
            tvDateRange.setText(text);
        } catch (Exception e) {
            tvDateRange.setText("");
        }
        tvTicketCount.setText("Còn " + (schedule.getNumberOfTicket() != null ? schedule.getNumberOfTicket() : "?") + " vé");
        tvStatus.setText(schedule.getStatus() != null ? schedule.getStatus().toString() : "Không xác định");

        LinearLayout staffCardContainer = findViewById(R.id.staffCardContainer);
        ApiService apiService = ApiClient.getApiService();
        String tourGuideId = schedule.getTourGuideId();
        if (tourGuideId != null && !tourGuideId.isEmpty()) {
            apiService.getStaffInfo(tourGuideId).enqueue(new Callback<SysUserDTO>() {
                @Override
                public void onResponse(Call<SysUserDTO> call, Response<SysUserDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SysUserDTO staff = response.body();
                        View staffCard = LayoutInflater.from(ScheduleDetailActivity.this).inflate(R.layout.item_admin_user, staffCardContainer, false);
                        // Bind avatar
                        ImageView imgAvatar = staffCard.findViewById(R.id.imgAvatar);
                        String avatarUrl = staff.getProfile() != null ? staff.getProfile().getAvatarUrl() : null;
                        Glide.with(ScheduleDetailActivity.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .circleCrop()
                                .into(imgAvatar);
                        // Bind name
                        TextView tvName = staffCard.findViewById(R.id.tvName);
                        tvName.setText(staff.getProfile() != null && staff.getProfile().getFullName() != null ? staff.getProfile().getFullName() : "Chưa cập nhật");
                        // Bind role
                        TextView tvRole = staffCard.findViewById(R.id.tvRole);
                        tvRole.setText(staff.getAccount() != null && staff.getAccount().getRoleName() != null ? staff.getAccount().getRoleName() : "");
                        // Bind status
                        TextView tvStatus = staffCard.findViewById(R.id.tvStatus);
                        if (staff.getAccount() != null && staff.getAccount().getStatus() != null) {
                            switch (staff.getAccount().getStatus()) {
                                case 1:
                                    tvStatus.setText("Đang hoạt động");
                                    break;
                                case 0:
                                    tvStatus.setText("Đã vô hiệu hóa");
                                    break;
                                default:
                                    tvStatus.setText("Không xác định");
                                    break;
                            }
                        } else {
                            tvStatus.setText("Chưa cập nhật");
                        }
                        // Ẩn nút edit/delete
                        staffCard.findViewById(R.id.btnEdit).setVisibility(View.GONE);
                        staffCard.findViewById(R.id.btnDelete).setVisibility(View.GONE);
                        staffCardContainer.addView(staffCard);
                    }
                }
                @Override
                public void onFailure(Call<SysUserDTO> call, Throwable t) {
                    // Không hiển thị gì nếu lỗi
                }
            });
        }

        FloatingActionButton fabAddGuide = findViewById(R.id.fabAddGuide);
        LinearLayout guideActionButtons = findViewById(R.id.guideActionButtons);
        if (tourGuideId == null || tourGuideId.isEmpty()) {
            fabAddGuide.setVisibility(View.VISIBLE);
            guideActionButtons.setVisibility(View.GONE);
        } else {
            fabAddGuide.setVisibility(View.GONE);
            guideActionButtons.setVisibility(View.VISIBLE);
        }
    }
} 