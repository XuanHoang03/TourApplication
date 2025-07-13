package com.hashmal.tourapplication.activity;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
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
    private YourTourDTO dto;

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
        progressBar.setVisibility(VISIBLE);

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

        dto = tour;

        TextView tvTourName = findViewById(R.id.tvTourName);
        tvTourName.setText(tour.getTourPackage().getPackageName());

        // Set ticket code
        TextView tvTicketCode = findViewById(R.id.tvTicketCode);
        tvTicketCode.setText("Mã vé: " + tour.getBooking().getId());

        // Set departure time
        TextView tvDepartureTime = findViewById(R.id.tvDepartureTime);
        String[] departureTime = (DataUtils.formatDateTimeString(tour.getTourSchedule().getStartTime(), true)).split("T");
        tvDepartureTime.setText("Khởi hành: " + departureTime[1] + " | " +departureTime[0] );

        // Set return time
        TextView tvReturnTime = findViewById(R.id.tvReturnTime);
        String[] returnTime = (DataUtils.formatDateTimeString(tour.getTourSchedule().getEndTime(), true)).split("T");
        tvReturnTime.setText("Về: " + returnTime[1] + " | " +returnTime[0] );

        TextView tvNumberOfTickets = findViewById(R.id.tvNumberOfTickets);
        tvNumberOfTickets.setText("Số vé: " + tour.getBooking().getQuantity());

        LinearLayout tvTour = findViewById(R.id.tvTour);
        tvTour.setOnClickListener(v -> {
            Intent tourProgIntent = new Intent(YourTourActivity.this, TourDetailActivity.class);
            TourResponseDTO tourResponseDTO = DataUtils.convertYourTourToTourResponse(tour);
            String tourJson = gson.toJson(tourResponseDTO);
            tourProgIntent.putExtra("tour", tourJson);
            startActivity(tourProgIntent);
        });
        Button btnAddRate = findViewById(R.id.btnAddRate);
        btnAddRate.setOnClickListener(v -> {
            // Thuc hien chuyen sang man danh gia Tour
            Intent reviewIntent = new Intent(YourTourActivity.this, TourReviewActivity.class);
            reviewIntent.putExtra("tourId", dto.getTour().getTourId());
            reviewIntent.putExtra("bookingId", String.valueOf( dto.getBooking().getId()));
            reviewIntent.putExtra("tourName", dto.getTour().getTourName());
            reviewIntent.putExtra("tourImageUrl", dto.getTour().getThumbnailUrl());
            reviewIntent.putExtra("tourDate", dto.getTourSchedule().getStartTime());
            startActivity(reviewIntent);

        });


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
                btnAddRate.setVisibility(VISIBLE);
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


        LinearLayout tvTourRoute = findViewById(R.id.tvTourRoute);
        tvTourRoute.setOnClickListener(v -> {

            Intent mapIntent = new Intent(YourTourActivity.this, TourRouteActivity.class);
            String locationString = gson.toJson(tour.getTourLocationList());
            mapIntent.putExtra("locations", locationString);
            startActivity(mapIntent);
        });
    }


}