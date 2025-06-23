package com.hashmal.tourapplication.activity;

import static android.view.View.GONE;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.service.LocalDataService;
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
import com.hashmal.tourapplication.adapter.UserBookingAdapter;
import com.hashmal.tourapplication.service.dto.UserBookingDTO;
import com.hashmal.tourapplication.adapter.TourScheduleListAdapter;
import com.hashmal.tourapplication.service.dto.UserDTO;

public class ScheduleDetailActivity extends AppCompatActivity implements UserBookingAdapter.OnBuyerActionListener {
    private TourScheduleResponseDTO currentSchedule; // Lưu lại lịch trình hiện tại
    private ApiService apiService;
    private String tourScheduleId, tourId;
    private UserBookingAdapter buyerAdapter;

    private LocalDataService localDataService;

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
        RecyclerView rvBuyers = findViewById(R.id.rvBuyers);

        apiService = ApiClient.getApiService();
        tourScheduleId = getIntent().getStringExtra("tourScheduleId");
        tourId = getIntent().getStringExtra("tourId");
        if (tourScheduleId == null || tourScheduleId.isEmpty()) {
            finish();
            return;
        }

        if (localDataService.getSysUser().getAccount().getRoleName().equals(RoleEnum.TOUR_GUIDE.name())) {
            btnRemoveGuide.setVisibility(GONE);
            btnChangeGuide.setVisibility(GONE);
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
                        guideActionButtons.setVisibility(GONE);
                    } else {
                        fabAddGuide.setVisibility(GONE);
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
                                    staffCard.findViewById(R.id.btnEdit).setVisibility(GONE);
                                    staffCard.findViewById(R.id.btnDelete).setVisibility(GONE);
                                    staffCardContainer.addView(staffCard);
                                }
                            }
                            @Override
                            public void onFailure(Call<SysUserDTO> call, Throwable t) { }
                        });
                    }

                    // Sau khi bind dữ liệu lịch trình, gọi API lấy danh sách người mua vé
                    apiService.getUserBookingsByTourSchedule(tourScheduleId).enqueue(new Callback<List<UserBookingDTO>>() {
                        @Override
                        public void onResponse(Call<List<UserBookingDTO>> call, Response<List<UserBookingDTO>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                buyerAdapter = new UserBookingAdapter(response.body(), ScheduleDetailActivity.this);
                                buyerAdapter.setOnItemClickListener(booking -> showUserProfileDialog(booking.getUser()));
                                rvBuyers.setLayoutManager(new LinearLayoutManager(ScheduleDetailActivity.this));
                                rvBuyers.setAdapter(buyerAdapter);
                            }
                        }
                        @Override
                        public void onFailure(Call<List<UserBookingDTO>> call, Throwable t) {
                            // Có thể show lỗi nếu muốn
                        }
                    });
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
                                    tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : GONE);
                                }
                                @Override
                                public void afterTextChanged(Editable s) {}
                            });
                            tvEmpty.setVisibility(guides.isEmpty() ? View.VISIBLE : GONE);
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

    @Override
    public void onModifyBooking(UserBookingDTO booking) {
        showLoading();
        apiService.getTourSchedulesByTourId(tourId).enqueue(new Callback<List<TourScheduleResponseDTO>>() {
            @Override
            public void onResponse(Call<List<TourScheduleResponseDTO>> call, Response<List<TourScheduleResponseDTO>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<TourScheduleResponseDTO> schedules = response.body();
                    // Loại bỏ lịch trình hiện tại
                    String currentScheduleId = booking.getBooking().getTourScheduleId();
                    List<TourScheduleResponseDTO> filtered = new java.util.ArrayList<>();
                    for (TourScheduleResponseDTO s : schedules) {
                        if (!s.getTourScheduleId().equals(currentScheduleId)) filtered.add(s);
                    }
                    if (filtered.isEmpty()) {
                        Toast.makeText(ScheduleDetailActivity.this, "Không có lịch trình khác để chuyển", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Hiển thị dialog chọn lịch trình
                    View dialogView = LayoutInflater.from(ScheduleDetailActivity.this).inflate(R.layout.dialog_select_schedule, null, false);
                    RecyclerView rvSchedules = dialogView.findViewById(R.id.rvSchedules);
                    Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);
                    AlertDialog dialog = new AlertDialog.Builder(ScheduleDetailActivity.this)
                        .setView(dialogView)
                        .create();
                    TourScheduleListAdapter adapter = new TourScheduleListAdapter(filtered, schedule -> {
                        dialog.dismiss();
                        showLoading();
                        apiService.modifyBooking(booking.getBooking().getBookingId(), schedule.getTourScheduleId(), null)
                            .enqueue(new Callback<BaseResponse>() {
                                @Override
                                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                    hideLoading();
                                    if (response.isSuccessful() && response.body() != null ) {
                                        BaseResponse res = response.body();
                                        if (res.getCode().equals(Code.SUCCESS.getCode())) {
                                            reloadBuyers();
                                        } else {
                                            Toast.makeText(ScheduleDetailActivity.this, "Chuyển lịch trình thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(ScheduleDetailActivity.this, "Chuyển lịch trình thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<BaseResponse> call, Throwable t) {
                                    hideLoading();
                                    Toast.makeText(ScheduleDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    });
                    rvSchedules.setLayoutManager(new LinearLayoutManager(ScheduleDetailActivity.this));
                    rvSchedules.setAdapter(adapter);
                    btnClose.setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                } else {
                    Toast.makeText(ScheduleDetailActivity.this, "Không lấy được danh sách lịch trình", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<TourScheduleResponseDTO>> call, Throwable t) {
                hideLoading();
                Toast.makeText(ScheduleDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelBooking(UserBookingDTO booking) {
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận hủy vé")
            .setMessage("Bạn có chắc chắn muốn hủy vé của " + (booking.getUser().getProfile() != null ? booking.getUser().getProfile().getFullName() : "người dùng") + "?")
            .setPositiveButton("Xác nhận", (dialog, which) -> {
                showLoading();
                apiService.modifyBooking(booking.getBooking().getBookingId(), null, "cancel")
                    .enqueue(new retrofit2.Callback<BaseResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                            hideLoading();
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(ScheduleDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                reloadBuyers();
                            } else {
                                Toast.makeText(ScheduleDetailActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<BaseResponse> call, Throwable t) {
                            hideLoading();
                            Toast.makeText(ScheduleDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void reloadBuyers() {
        apiService.getUserBookingsByTourSchedule(tourScheduleId).enqueue(new retrofit2.Callback<List<UserBookingDTO>>() {
            @Override
            public void onResponse(retrofit2.Call<List<UserBookingDTO>> call, retrofit2.Response<List<UserBookingDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    buyerAdapter.updateData(response.body());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<UserBookingDTO>> call, Throwable t) {
                // Không reload được
            }
        });
    }

    private void showLoading() {
        // Hiện loading, ví dụ: progressBar.setVisibility(View.VISIBLE);
    }
    private void hideLoading() {
        // Ẩn loading, ví dụ: progressBar.setVisibility(View.GONE);
    }

    private void showUserProfileDialog(UserDTO user) {
        if (user == null || user.getProfile() == null) {
            Toast.makeText(this, "Không có thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_profile, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        ImageView imgAvatar = dialogView.findViewById(R.id.imgAvatar);
        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvEmail = dialogView.findViewById(R.id.tvEmail);
        TextView tvPhone = dialogView.findViewById(R.id.tvPhone);
        TextView tvAddress = dialogView.findViewById(R.id.tvAddress);
        Button btnClose = dialogView.findViewById(R.id.btnClose);

        // Load avatar
        Glide.with(this)
                .load(user.getProfile().getAvatarUrl())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(imgAvatar);

        // Set info
        tvName.setText(user.getProfile().getFullName());
        tvEmail.setText(user.getProfile().getEmail());
        tvPhone.setText(user.getProfile().getPhoneNumber());
        tvAddress.setText(user.getProfile().getAddress());

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}