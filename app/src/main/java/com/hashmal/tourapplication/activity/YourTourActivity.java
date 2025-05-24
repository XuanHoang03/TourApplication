package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;

public class YourTourActivity extends AppCompatActivity {
    private LocalDataService localDataService;
    private ApiService apiService;
    private Intent intent;
    private String tourId = "";
    private Long bookingId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_your_tour);

        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);
        intent = getIntent();
        tourId = intent.getStringExtra("tourId");
        bookingId = intent.getLongExtra("bookingId",-1);

    }

    public void getYourTour() {

    }
}