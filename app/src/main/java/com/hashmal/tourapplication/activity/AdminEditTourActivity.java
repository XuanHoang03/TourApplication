package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.utils.DataUtils;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import com.hashmal.tourapplication.service.dto.CreateTourRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditTourActivity extends AppCompatActivity {
    private TourResponseDTO tour;
    private List<YourTourDTO.Location> allLocations = new ArrayList<>();
    private List<CreateTourRequest.VisitOrderDTO> selectedLocations = new ArrayList<>();
    private ApiService apiService;
    private LinearLayout layoutSelectedLocations;
    private TextView tvSelectedLocations;
    private Button btnPickLocations;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_tour);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        String tourJson = getIntent().getStringExtra("tour");
        tour = new com.google.gson.Gson().fromJson(tourJson, TourResponseDTO.class);
        ImageView img = findViewById(R.id.imgThumbnail);
        TextInputEditText edtName = findViewById(R.id.edtTourName);
        TextInputEditText edtType = findViewById(R.id.edtTourType);
        TextInputEditText edtDesc = findViewById(R.id.edtTourDescription);
        TextInputEditText edtNum = findViewById(R.id.edtNumberOfPeople);
        TextInputEditText edtDuration = findViewById(R.id.edtDuration);
        TextInputEditText edtStart = findViewById(R.id.edtStart);
        TextInputEditText edtEnd = findViewById(R.id.edtEnd);
        layoutSelectedLocations = findViewById(R.id.layoutSelectedLocations);
        tvSelectedLocations = findViewById(R.id.tvSelectedLocations);
        Button btnSave = findViewById(R.id.btnSaveTour);
        btnPickLocations = findViewById(R.id.btnPickLocations);
        Glide.with(this).load(tour.getThumbnailUrl()).placeholder(R.drawable.ic_tour).into(img);
        edtName.setText(tour.getTourName());
        edtType.setText(tour.getTourType());
        edtDesc.setText(tour.getTourDescription());
        edtNum.setText(String.valueOf(tour.getNumberOfPeople()));
        edtDuration.setText(tour.getDuration());
        edtStart.setText(DataUtils.getStartOrEndTime(tour.getCurrentStartTime()));
        edtEnd.setText(DataUtils.getStartOrEndTime(tour.getCurrentEndTime()));
        edtStart.setOnClickListener(v -> showTimePicker(edtStart));
        edtEnd.setOnClickListener(v -> showTimePicker(edtEnd));


        btnPickLocations.setOnClickListener(v -> showPickLocationsDialog());

        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Đã lưu (demo)", Toast.LENGTH_SHORT).show();
            finish();
        });
        apiService = ApiClient.getApiService();
        loadAllLocations().thenAccept(success -> {
                runOnUiThread(() -> {
                    selectedLocations.clear();
                    LoadCurrentLocationList();
                    updateSelectedLocationsText();
                });
        });
    }

    private void showTimePicker(TextInputEditText target) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);
        android.app.TimePickerDialog timePicker = new android.app.TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String timeStr = String.format("%02d:%02d", hourOfDay, minute1);
            target.setText(timeStr);
        }, hour, minute, true);
        timePicker.show();
    }

    private void showPickLocationsDialog() {
        showAddLocationDialog();
    }

    private CompletableFuture<Boolean> loadAllLocations() {

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        apiService.getAllLocations().enqueue(new Callback<List<YourTourDTO.Location>>() {
            @Override
            public void onResponse(Call<List<YourTourDTO.Location>> call, Response<List<YourTourDTO.Location>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allLocations = response.body();
                    future.complete(true);
                } else {
                    Toast.makeText(AdminEditTourActivity.this, "Không tải được danh sách địa điểm", Toast.LENGTH_SHORT).show();
                    future.complete(false);
                }
            }

            @Override
            public void onFailure(Call<List<YourTourDTO.Location>> call, Throwable t) {
                Toast.makeText(AdminEditTourActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                future.complete(false);
            }
        });
        return future;
    }

    private void LoadCurrentLocationList() {
        List<LocationDTO> list =  tour.getLocations();
        for (LocationDTO locationDTO : list ) {
            CreateTourRequest.VisitOrderDTO dto = new CreateTourRequest.VisitOrderDTO();
            dto.setLocationId(locationDTO.getId());
            dto.setOrderNumber(locationDTO.getVisitOrder());
            selectedLocations.add(dto);
        }

    }

    private void showAddLocationDialog() {
        List<YourTourDTO.Location> available = new ArrayList<>();
        for (YourTourDTO.Location loc : allLocations) {
            boolean already = false;
            for (CreateTourRequest.VisitOrderDTO dto : selectedLocations) {
                if (dto.getLocationId() != null && dto.getLocationId().equals(loc.getId())) {
                    already = true;
                    break;
                }
            }
            if (!already) available.add(loc);
        }
        if (available.isEmpty()) {
            Toast.makeText(this, "Đã chọn hết địa điểm!", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[available.size()];
        for (int i = 0; i < available.size(); i++) names[i] = available.get(i).getName();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn địa điểm để thêm")
                .setItems(names, (dialog, which) -> {
                    YourTourDTO.Location loc = available.get(which);
                    selectedLocations.add(new CreateTourRequest.VisitOrderDTO(loc.getId(), selectedLocations.size() + 1));
                    updateSelectedLocationsText();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateSelectedLocationsText() {
        layoutSelectedLocations.removeAllViews();
        if (selectedLocations.isEmpty()) {
            tvSelectedLocations.setText("Chưa chọn địa điểm nào");
            return;
        }
        tvSelectedLocations.setText("");
        for (int i = 0; i < selectedLocations.size(); i++) {
            final int index = i;
            Long id = selectedLocations.get(i).getLocationId();
            String name = "";
            for (YourTourDTO.Location loc : allLocations) {
                if (loc.getId() == id) {
                    name = loc.getName();
                    break;
                }
            }
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            TextView tv = new TextView(this);
            tv.setText((i + 1) + ". " + name);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            Button btnRemove = new Button(this);
            btnRemove.setText("X");
            btnRemove.setTextSize(12);
            btnRemove.setPadding(16, 0, 16, 0);
            btnRemove.setOnClickListener(v -> {
                selectedLocations.remove(index);
                updateSelectedLocationsText();
            });
            row.addView(tv);
            row.addView(btnRemove);
            layoutSelectedLocations.addView(row);
        }
    }

}
