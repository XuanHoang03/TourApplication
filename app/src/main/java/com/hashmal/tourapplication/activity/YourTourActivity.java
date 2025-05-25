package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourTourActivity extends AppCompatActivity {
    private LocalDataService localDataService;
    private ApiService apiService;
    private Intent intent;
    private String tourId = "";
    private Long bookingId = null;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_tour);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);
        intent = getIntent();
        bookingId = intent.getLongExtra("bookingId", -1);

        // Load tour details
        getYourTour();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getYourTour() {
        if (bookingId == null) {
            Toast.makeText(this, "Invalid tour information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);

        // Call API to get tour details
        apiService.getYourTour(bookingId).enqueue(new Callback<YourTourDTO>() {
            @Override
            public void onResponse(Call<YourTourDTO> call, Response<YourTourDTO> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    YourTourDTO tour = response.body();
                    updateTourDetails(tour);
                } else {
                    Toast.makeText(YourTourActivity.this, "Failed to load tour information", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<YourTourDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(YourTourActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateTourDetails(YourTourDTO tour) {
        // Set tour name
        TextView tvTourName = findViewById(R.id.tvTourName);
        tvTourName.setText(tour.getTourPackage().getPackageName());

        // Set ticket code
        TextView tvTicketCode = findViewById(R.id.tvTicketCode);
        tvTicketCode.setText("Mã vé: " + tour.getBooking().getId());

        // Set departure time
        TextView tvDepartureTime = findViewById(R.id.tvDepartureTime);
        String departureTime = (DataUtils.formatDateTimeString(tour.getTourSchedule().getStartTime()));

        // Set return time
        TextView tvReturnTime = findViewById(R.id.tvReturnTime);
        String returnTime = (DataUtils.formatDateTimeString(tour.getTourSchedule().getEndTime()));

        TextView tvNumberOfTickets = findViewById(R.id.tvNumberOfTickets);
        tvNumberOfTickets.setText("Số vé: " + tour.getBooking().getQuantity());

        // Set total price
        TextView tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText("Tổng tiền: " + DataUtils.formatCurrency(tour.getBooking().getTotalPrice()));

        // Set status
        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText(DataUtils.convertStatusFromInt( tour.getTourSchedule().getStatus()));
        // Set status color based on status
        switch (tour.getTourSchedule().getStatus()) {
            case 1:
                tvStatus.setBackgroundTintList(getColorStateList(R.color.status_confirmed));
                break;
            case 0:
                tvStatus.setBackgroundTintList(getColorStateList(R.color.status_pending));
                break;
            case -1:
                tvStatus.setBackgroundTintList(getColorStateList(R.color.status_cancelled));
                break;
            default:
                tvStatus.setBackgroundTintList(getColorStateList(R.color.status_default));
                break;
        }
    }


}