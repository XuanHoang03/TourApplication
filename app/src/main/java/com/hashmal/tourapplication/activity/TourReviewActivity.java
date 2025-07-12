package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.CreateReviewRequest;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.YourTourDTO;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourReviewActivity extends AppCompatActivity {
    
    private Toolbar toolbar;
    private ImageView ivTourImage;
    private TextView tvTourName, tvTourDate, tvBookingId, tvRatingText;
    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private Button btnSubmitReview;
    
    private ApiService apiService;
    private LocalDataService localDataService;
    
    private String tourId;
    private String bookingId;
    private String tourName;
    private String tourImageUrl;
    private String tourDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_review);
        
        // Initialize services
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);
        
        // Get data from intent
        getDataFromIntent();
        
        // Initialize views
        initializeViews();
        
        // Setup toolbar
        setupToolbar();
        
        // Setup rating listeners
        setupRatingListeners();
        
        // Setup submit button
        setupSubmitButton();
        
        // Display tour info
        displayTourInfo();
    }
    
    private void getDataFromIntent() {
        Intent intent = getIntent();
        tourId = intent.getStringExtra("tourId");
        bookingId = intent.getStringExtra("bookingId");
        tourName = intent.getStringExtra("tourName");
        tourImageUrl = intent.getStringExtra("tourImageUrl");
        tourDate = intent.getStringExtra("tourDate");
        
        if (tourId == null || bookingId == null) {
            Toast.makeText(this, "Thông tin tour không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        ivTourImage = findViewById(R.id.ivTourImage);
        tvTourName = findViewById(R.id.tvTourName);
        tvTourDate = findViewById(R.id.tvTourDate);
        tvBookingId = findViewById(R.id.tvBookingId);
        tvRatingText = findViewById(R.id.tvRatingText);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Đánh giá Tour");
        }
    }
    
    private void setupRatingListeners() {
        // Main rating bar listener
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            updateRatingText(rating);
        });
        
        // Set default rating text
        updateRatingText(5.0f);
    }
    
    private void updateRatingText(float rating) {
        String ratingText;
        int ratingInt = (int) rating;
        switch (ratingInt) {
            case 5:
                ratingText = "Tuyệt vời";
                break;
            case 4:
                ratingText = "Rất tốt";
                break;
            case 3:
                ratingText = "Tốt";
                break;
            case 2:
                ratingText = "Trung bình";
                break;
            case 1:
                ratingText = "Kém";
                break;
            default:
                ratingText = "Chưa đánh giá";
                break;
        }
        tvRatingText.setText(ratingText);
    }
    
    private void setupSubmitButton() {
        btnSubmitReview.setOnClickListener(v -> {
            if (validateInput()) {
                submitReview();
            }
        });
    }
    
    private boolean validateInput() {
        String comment = etComment.getText().toString().trim();
        
        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nhận xét của bạn", Toast.LENGTH_SHORT).show();
            etComment.requestFocus();
            return false;
        }
        
        if (comment.length() < 10) {
            Toast.makeText(this, "Nhận xét phải có ít nhất 10 ký tự", Toast.LENGTH_SHORT).show();
            etComment.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void submitReview() {
        // Show loading
        btnSubmitReview.setEnabled(false);
        btnSubmitReview.setText("Đang gửi...");
        
        // Get current user
        String userId = localDataService.getCurrentUser().getAccount().getAccountId();
        
        // Create review request
        CreateReviewRequest request = new CreateReviewRequest(
                userId,
                tourId,
                bookingId.toString(),
                (int) ratingBar.getRating(),
                etComment.getText().toString().trim()
        );
        
        // Call API
        apiService.createReview(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                btnSubmitReview.setEnabled(true);
                btnSubmitReview.setText("Gửi đánh giá");
                
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse baseResponse = response.body();
                    if (baseResponse.getCode().equals(Code.SUCCESS.getCode())) {
                        Toast.makeText(TourReviewActivity.this, "Cảm ơn bạn đã đánh giá !", Toast.LENGTH_LONG).show();
                        
                        // Return result to previous activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("reviewSubmitted", true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        new AlertDialog.Builder(TourReviewActivity.this)
                                .setTitle("Thông báo")
                                .setMessage("Bạn đã đánh giá chuyến Tour này rồi !")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .show();
                    }
                } else {
                    Toast.makeText(TourReviewActivity.this, "Có lỗi xảy ra khi gửi đánh giá", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                btnSubmitReview.setEnabled(true);
                btnSubmitReview.setText("Gửi đánh giá");
                Toast.makeText(TourReviewActivity.this, "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayTourInfo() {
        // Set tour name
        tvTourName.setText(tourName != null ? tourName : "Tour du lịch");
        
        // Set tour date
        if (tourDate != null) {
            tvTourDate.setText("Ngày đi: " + tourDate);
        }
        
        // Set booking ID
        tvBookingId.setText("Mã đặt tour: #" + bookingId);
        
        // Load tour image
        if (tourImageUrl != null && !tourImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(tourImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(ivTourImage);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        // Ask user if they want to discard the review
        new AlertDialog.Builder(this)
                .setTitle("Hủy đánh giá")
                .setMessage("Bạn có chắc chắn muốn hủy đánh giá này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    super.onBackPressed();
                })
                .setNegativeButton("Không", null)
                .show();
    }
} 