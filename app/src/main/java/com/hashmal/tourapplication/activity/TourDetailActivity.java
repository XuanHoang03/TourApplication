package com.hashmal.tourapplication.activity;

import static android.app.ProgressDialog.show;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.TourPackageAdapter;
import com.hashmal.tourapplication.adapter.TourScheduleAdapter;
import com.hashmal.tourapplication.dialog.CustomDialog;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.model.TourPackage;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.Booking;
import com.hashmal.tourapplication.service.dto.CreateBookingRequest;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.PaymentRequest;
import com.hashmal.tourapplication.service.dto.PaymentResponse;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TourDetailActivity extends AppCompatActivity implements TourPackageAdapter.OnPackageClickListener, TourScheduleAdapter.OnScheduleClickListener {
    private ImageView tourImage;
    private TextView tourName, tourDescription, tourDuration;
    private TextView tourRate;
    private RecyclerView packagesRecyclerView;
    private RecyclerView schedulesRecyclerView;
    private TourPackageAdapter packageAdapter;
    private TourScheduleAdapter scheduleAdapter;
    private List<TourPackageDTO> packages = new ArrayList<>();
    private List<TourScheduleResponseDTO> schedules = new ArrayList<>();
    private Gson gson = new Gson();
    private Button bookingButton;
    private TourPackageDTO selectedPackage;
    private TourScheduleResponseDTO selectedSchedule;

    private ApiService apiService;
    private LocalDataService localDataService;
    TourResponseDTO currentTourInfo;
    private Long bookingId;

    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
//                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    updatePaymentStatusOfBooking(bookingId, 1);
                } else {
                    updatePaymentStatusOfBooking(bookingId, -1);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        // Initialize views
        initializeViews();

        // Get tour data from intent
        String tourJson = getIntent().getStringExtra("tour");
        TourResponseDTO tour = gson.fromJson(tourJson, TourResponseDTO.class);
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);

        loadTourInfo(tour.getTourId());


        // Setup booking button
        bookingButton.setOnClickListener(v -> {
            if (localDataService.getCurrentUser().getAccount().getAccountId().equals( RoleEnum.GUEST.getRoleName())) {
                requestLogin();
            }
            if (selectedPackage == null) {
                Toast.makeText(this, "Vui lòng chọn gói tour", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSchedule == null) {
                Toast.makeText(this, "Vui lòng chọn lịch trình", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentTourInfo.getStatus() == -1) {
                Toast.makeText(this, "Tour này không còn hoạt động nữa!", Toast.LENGTH_SHORT).show();
                return;
            }

            showBookingBottomSheet();
        });
    }

    public void loadTourInfo(String tourId) {
        apiService.getTourInfo(tourId).enqueue(new Callback<TourResponseDTO>() {
            @Override
            public void onResponse(Call<TourResponseDTO> call, Response<TourResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTourInfo = response.body();
                    displayTourDetails(currentTourInfo);
                    packages = currentTourInfo.getPackages();
                    setupPackagesRecyclerView(packages);
                } else {
                    currentTourInfo = null;
                }
            }

            @Override
            public void onFailure(Call<TourResponseDTO> call, Throwable t) {
                Toast.makeText(TourDetailActivity.this, "Error when calling api get tour info", Toast.LENGTH_SHORT).show();
                currentTourInfo = null;
            }
        });
    }

    private void initializeViews() {
        tourImage = findViewById(R.id.tourImage);
        tourName = findViewById(R.id.tourName);
        tourDescription = findViewById(R.id.tourDescription);
        tourDuration = findViewById(R.id.tourDuration);
        tourRate = findViewById(R.id.tourRate);
        packagesRecyclerView = findViewById(R.id.packagesRecyclerView);
        schedulesRecyclerView = findViewById(R.id.schedulesRecyclerView);
        bookingButton = findViewById(R.id.bookingButton);

        // Setup click listener for tour rate
        tourRate.setOnClickListener(v -> openReviewsActivity());

        // Setup schedules RecyclerView
        scheduleAdapter = new TourScheduleAdapter(this, schedules, this);
        schedulesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        schedulesRecyclerView.setAdapter(scheduleAdapter);
    }

    private void openReviewsActivity() {
        if (currentTourInfo != null) {
            Intent intent = new Intent(this, TourReviewsActivity.class);
            intent.putExtra("tourId", currentTourInfo.getTourId());
            intent.putExtra("tourName", currentTourInfo.getTourName());
            startActivity(intent);
        }
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
        tourDuration.setText(DataUtils.getTourDuration(tour.getDuration()));
        tourRate.setText( tour.getRatePoint() + "/5 (" + tour.getRateCount() +" đánh giá)");
//        tourRating.setRating(tour.get());

        // Load location images
        loadTourSchedules(tour.getTourId());

        loadLocationImages(tour.getLocations());
    }

    private void loadTourSchedules(String tourId) {
        apiService.getTourSchedulesByTourId(tourId).enqueue(new Callback<List<TourScheduleResponseDTO>>() {
            @Override
            public void onResponse(Call<List<TourScheduleResponseDTO>> call, Response<List<TourScheduleResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TourScheduleResponseDTO> newSchedules = response.body();
                    schedules.clear();
                    schedules.addAll(newSchedules);
                    scheduleAdapter.updateSchedules(schedules);
                    Log.d("TourDetail", "Successfully loaded " + schedules.size() + " tour schedules");
                } else {
                    Log.e("TourDetail", "Failed to load tour schedules: " + response.code());
                    Toast.makeText(TourDetailActivity.this,
                            "Failed to load tour schedules",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TourScheduleResponseDTO>> call, Throwable t) {
                Log.e("TourDetail", "Error loading tour schedules", t);
                Toast.makeText(TourDetailActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
                    if (localDataService.getCurrentUser().getAccount().getAccountId().equals( RoleEnum.GUEST.getRoleName())) {
                        requestLogin();
                        return;
                    }
                    selectedPackage = tourPackage;
                    Toast.makeText(TourDetailActivity.this,
                            "Đã chọn gói: " + tourPackage.getPackageName(),
                            Toast.LENGTH_SHORT).show();
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
    public void onScheduleClick(TourScheduleResponseDTO schedule) {
        if (localDataService.getCurrentUser().getAccount().getAccountId().equals( RoleEnum.GUEST.getRoleName())) {
            requestLogin();
        } else {
            selectedSchedule = schedule;
            Toast.makeText(this, "Đã chọn lịch trình", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPackageClick(TourPackageDTO tourPackage) {

    }

    private void showBookingBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_booking, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Set up the views in the bottom sheet
        TextView selectedPackageText = bottomSheetView.findViewById(R.id.selectedPackageText);
        TextView selectedScheduleText = bottomSheetView.findViewById(R.id.selectedScheduleText);
        TextView availableTickets = bottomSheetView.findViewById(R.id.ticketAvailable);
        EditText quantityInput = bottomSheetView.findViewById(R.id.quantityInput);
        Button confirmButton = bottomSheetView.findViewById(R.id.confirmBookingButton);

        // Set the selected package and schedule information
        selectedPackageText.setText(selectedPackage.getPackageName() + "\n" +
                "Giá: " + DataUtils.formatCurrency(selectedPackage.getPrice()));

        selectedScheduleText.setText( selectedSchedule.getStartTime() + " - " +
                selectedSchedule.getEndTime());

        availableTickets.setText(selectedSchedule.getNumberOfTicket().toString());

        // Set up confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();
            if (quantityStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng người", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Số lượng người phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button to prevent multiple clicks
            confirmButton.setEnabled(false);
            confirmButton.setText("Processing...");

            String userId = localDataService.getCurrentUser().getAccount().getAccountId();
            String scheduleId = selectedSchedule.getTourScheduleId();
            Long packageId = selectedPackage.getId();

            CreateBookingRequest request = new CreateBookingRequest();
            request.setUserId(userId);
            request.setTourPackageId(packageId);
            request.setTourScheduleId(scheduleId);
            request.setQuantity(quantity);
            request.setPaymentMethod(null);

            apiService.createBooking(request).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful()) {

                        BaseResponse res = response.body();
                        if (!res.getCode().equals(Code.SUCCESS.getCode())) {
                            Toast.makeText(TourDetailActivity.this, res.getMessage(), Toast.LENGTH_LONG).show();
                            confirmButton.setEnabled(true);
                            confirmButton.setText("Xác nhận đặt Tour");
                            return;
                        }
                        String bookingJson = gson.toJson(res.getData());
                        Booking booking = gson.fromJson(bookingJson, Booking.class);
                        bookingId = booking.getId();
                        String orderInfo = String.format("B%09d", bookingId);
                        PaymentRequest paymentRequest = new PaymentRequest(booking.getTotalPrice(), orderInfo);
                        Call<PaymentResponse> callPayment = apiService.createPayment(paymentRequest);

                        callPayment.enqueue(new Callback<PaymentResponse>() {
                            @Override
                            public void onResponse(Call<PaymentResponse> callPayment, Response<PaymentResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String paymentUrl = response.body().getPaymentUrl();
                                    openVNPayWebView(paymentUrl);
                                } else {
                                    Toast.makeText(TourDetailActivity.this, "Không thể tạo thanh toán", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<PaymentResponse> callPayment, Throwable t) {
                                Toast.makeText(TourDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        bottomSheetDialog.dismiss();
                        // TODO: Navigate to booking success screen or booking list


                    } else {
                        String errorMessage = "Đặt tour thất bại: ";
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += response.errorBody().string();
                            } catch (Exception e) {
                                errorMessage += "Lỗi không xác định";
                            }
                        }
                        Toast.makeText(TourDetailActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                    // Re-enable button
                    confirmButton.setEnabled(true);
                    confirmButton.setText("Xác nhận đặt tour");
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Toast.makeText(TourDetailActivity.this,
                            "Lỗi kết nối: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    // Re-enable button
                    confirmButton.setEnabled(true);
                    confirmButton.setText("Xác nhận đặt tour");
                }
            });
        });

        bottomSheetDialog.show();
    }

    private void openVNPayWebView(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("payment_url", url);
        paymentLauncher.launch(intent);
    }

    private void updatePaymentStatusOfBooking(Long bookingId, Integer status) {
        apiService.updatePaymentStatus(bookingId, status)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BaseResponse res = response.body();
                            if (res.getCode().equals(Code.SUCCESS.getCode())) {
                                String message = (status == 1) ? "Bạn đã thanh toán vé thành công." : "Bạn đã hủy thanh toán vé.";
                                new CustomDialog.Builder(TourDetailActivity.this)
                                        .setTitle("Thông báo")
                                        .setMessage(message)
                                        .setIcon(getDrawable(R.drawable.ic_info))
                                        .setPositiveButton((status == 1) ? "Quay về trang chủ" : "Đóng", dialog -> {
//                                             Xử lý khi click nút xác nhận
                                            if (status == 1) {
                                                finish();
                                            }
                                        })
                                        .setSingleButtonMode(true)
                                        .show();
                            } else {
//                                Toast.makeText(TourDetailActivity.this,
//                                        res.getMessage(),
//                                        Toast.LENGTH_LONG).show();
                                new CustomDialog.Builder(TourDetailActivity.this)
                                        .setTitle("Thông báo")
                                        .setMessage("Thanh toán vé thất bại!")
                                        .setIcon(getDrawable(R.drawable.ic_info))
                                        .setPositiveButton("Đóng", dialog -> {

                                        })
                                        .setSingleButtonMode(true)
                                        .show();
                            }
                        } else {
                            Toast.makeText(TourDetailActivity.this,
                                    "Không thể cập nhật trạng thái thanh toán",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(TourDetailActivity.this,
                                "Lỗi kết nối: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void requestLogin() {
        new AlertDialog.Builder(this)
                .setMessage("Bạn cần phải đăng nhập để thực hiện chức năng này!")
                .setPositiveButton("Đăng nhập ngay", (dialog, which) -> {
                    localDataService.clearUserData();
                    Intent intent = new Intent(TourDetailActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                })
                .setNegativeButton("Tôi chưa muốn đăng nhập", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        }
}