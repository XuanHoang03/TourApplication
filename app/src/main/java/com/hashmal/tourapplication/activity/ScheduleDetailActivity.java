package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
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
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import java.util.List;
import android.text.TextWatcher;
import android.text.Editable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hashmal.tourapplication.adapter.GuideSelectAdapter;

public class ScheduleDetailActivity extends AppCompatActivity {
    private TourScheduleResponseDTO currentSchedule; // Lưu lại lịch trình hiện tại
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

        TextView tvName = findViewById(R.id.tvScheduleName);
        TextView tvDateRange = findViewById(R.id.tvDateRange);
        TextView tvTicketCount = findViewById(R.id.tvTicketCount);
        TextView tvStatus = findViewById(R.id.tvStatus);
        LinearLayout staffCardContainer = findViewById(R.id.staffCardContainer);
        FloatingActionButton fabAddGuide = findViewById(R.id.fabAddGuide);
        LinearLayout guideActionButtons = findViewById(R.id.guideActionButtons);
        Button btnRemoveGuide = findViewById(R.id.btnRemoveGuide);
        Button btnChangeGuide = findViewById(R.id.btnChangeGuide);

        ApiService apiService = ApiClient.getApiService();
        String tourScheduleId = getIntent().getStringExtra("tourScheduleId");
        if (tourScheduleId == null || tourScheduleId.isEmpty()) {
            finish();
            return;
        }
        apiService.getTourSchedule(tourScheduleId).enqueue(new Callback<TourScheduleResponseDTO>() {
            @Override
            public void onResponse(Call<TourScheduleResponseDTO> call, Response<TourScheduleResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TourScheduleResponseDTO schedule = response.body();
                    currentSchedule = schedule;
                    // Bind dữ liệu như trước
                    tvName.setText(schedule.getTourScheduleId() != null ? schedule.getTourScheduleId() : "Lịch trình tour");
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

                    // Staff logic
                    staffCardContainer.removeAllViews();
                    String tourGuideId = schedule.getTourGuideId();
                    if (tourGuideId == null || tourGuideId.isEmpty()) {
                        fabAddGuide.setVisibility(View.VISIBLE);
                        fabAddGuide.setOnClickListener( v -> addTourGuide());
                        guideActionButtons.setVisibility(View.GONE);
                    } else {
                        fabAddGuide.setVisibility(View.GONE);
                        guideActionButtons.setVisibility(View.VISIBLE);
                        btnChangeGuide.setOnClickListener(v -> addTourGuide());
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
                            public void onFailure(Call<SysUserDTO> call, Throwable t) { }
                        });
                    }
                } else {
                    finish();
                }
            }

            private void addTourGuide() {
                if (currentSchedule == null) return;
                ApiService apiService = ApiClient.getApiService();
                String startTime = currentSchedule.getStartTime();
                String endTime = currentSchedule.getEndTime();
                apiService.getAvailableTourGuide(startTime, endTime).enqueue(new Callback<List<SysUserDTO>>() {
                    @Override
                    public void onResponse(Call<List<SysUserDTO>> call, Response<List<SysUserDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<SysUserDTO> guides = response.body();
                            View dialogView = LayoutInflater.from(ScheduleDetailActivity.this).inflate(R.layout.dialog_select_guide, null, false);
                            EditText etSearch = dialogView.findViewById(R.id.etSearchGuide);
                            RecyclerView rvGuides = dialogView.findViewById(R.id.rvGuides);
                            TextView tvEmpty = dialogView.findViewById(R.id.tvEmpty);
                            Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);
                            final AlertDialog[] dialog = new AlertDialog[1];
                            GuideSelectAdapter adapter = new GuideSelectAdapter(guides, guide -> {
                                String guideId = guide.getAccount() != null ? guide.getAccount().getAccountId() : null;
                                if (guideId != null) {
                                    apiService.modifyTourGuideForTour(currentSchedule.getTourScheduleId(), guideId).enqueue(new Callback<BaseResponse>() {
                                        @Override
                                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                            if (response.isSuccessful() && response.body() != null && response.body().getCode().equals(Code.SUCCESS.getCode())) {
                                                Toast.makeText(ScheduleDetailActivity.this, "Đã thêm hướng dẫn viên", Toast.LENGTH_SHORT).show();
                                                if (dialog[0] != null) dialog[0].dismiss();
                                                recreate();
                                            } else {
                                                Toast.makeText(ScheduleDetailActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                                            Toast.makeText(ScheduleDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            rvGuides.setLayoutManager(new LinearLayoutManager(ScheduleDetailActivity.this));
                            rvGuides.setAdapter(adapter);
                            // Filter
                            etSearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    String query = s.toString().toLowerCase();
                                    List<SysUserDTO> filtered = new java.util.ArrayList<>();
                                    for (SysUserDTO g : guides) {
                                        String name = g.getProfile() != null && g.getProfile().getFullName() != null ? g.getProfile().getFullName().toLowerCase() : "";
                                        String phone = g.getProfile() != null && g.getProfile().getPhoneNumber() != null ? g.getProfile().getPhoneNumber() : "";
                                        if (name.contains(query) || phone.contains(query)) {
                                            filtered.add(g);
                                        }
                                    }
                                    adapter.updateData(filtered);
                                    tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                                }
                                @Override
                                public void afterTextChanged(Editable s) {}
                            });
                            tvEmpty.setVisibility(guides.isEmpty() ? View.VISIBLE : View.GONE);
                            dialog[0] = new AlertDialog.Builder(ScheduleDetailActivity.this)
                                .setView(dialogView)
                                .create();
                            btnClose.setOnClickListener(v -> {
                                if (dialog[0] != null) dialog[0].dismiss();
                            });
                            dialog[0].show();
                        } else {
                            Toast.makeText(ScheduleDetailActivity.this, "Không có hướng dẫn viên khả dụng", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<SysUserDTO>> call, Throwable t) {
                        Toast.makeText(ScheduleDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<TourScheduleResponseDTO> call, Throwable t) {
                finish();
            }
        });

        btnRemoveGuide.setOnClickListener(v -> {
            apiService.modifyTourGuideForTour(tourScheduleId, null).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode().equals(Code.SUCCESS.getCode())) {
                        Toast.makeText(ScheduleDetailActivity.this, "Đã xóa hướng dẫn viên", Toast.LENGTH_SHORT).show();
                        recreate();
                    } else {
                        Toast.makeText(ScheduleDetailActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Toast.makeText(ScheduleDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}