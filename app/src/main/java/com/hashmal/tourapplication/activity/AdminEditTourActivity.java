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
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.utils.DataUtils;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import com.hashmal.tourapplication.service.dto.CreateTourRequest;
import com.hashmal.tourapplication.service.dto.UpdateTourRequest;
import com.hashmal.tourapplication.enums.Code;

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
            String name = edtName.getText().toString().trim();
            String type = edtType.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();
            String numStr = edtNum.getText().toString().trim();
            String duration = edtDuration.getText().toString().trim();
            String start = edtStart.getText().toString().trim();
            String end = edtEnd.getText().toString().trim();
            if (name.isEmpty() || type.isEmpty() || numStr.isEmpty() || duration.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            Integer numPeople;
            try { numPeople = Integer.parseInt(numStr); } catch (Exception e) { edtNum.setError("Sai định dạng"); return; }
            // Tạo request
            UpdateTourRequest req = new UpdateTourRequest(
                name,
                type,
                desc,
                numPeople,
                duration,
                tour.getThumbnailUrl(), // Giữ nguyên ảnh cũ, nếu muốn đổi ảnh thì xử lý thêm
                start,
                end,
                new ArrayList<>(selectedLocations)
            );
            btnSave.setEnabled(false);
            apiService.updateTour(tour.getTourId(), req).enqueue(new retrofit2.Callback<BaseResponse>() {
                @Override
                public void onResponse(retrofit2.Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && Code.SUCCESS.getCode().equals(response.body().getCode())) {
                        Toast.makeText(AdminEditTourActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        // Cập nhật lại biến tour với dữ liệu mới
                        tour.setTourName(name);
                        tour.setTourType(type);
                        tour.setTourDescription(desc);
                        tour.setNumberOfPeople(Long.valueOf(numPeople));
                        tour.setDuration(duration);
                        tour.setCurrentStartTime(start);
                        tour.setCurrentEndTime(end);
                        // Cập nhật lại danh sách địa điểm
                        List<LocationDTO> newLocs = new ArrayList<>();
                        for (CreateTourRequest.VisitOrderDTO dto : selectedLocations) {
                            LocationDTO loc = new LocationDTO();
                            loc.setId(dto.getLocationId());
                            loc.setVisitOrder(dto.getOrderNumber());
                            // Gán tên nếu có trong allLocations
                            for (YourTourDTO.Location l : allLocations) {
                                if (l.getId() == (dto.getLocationId())) {
                                    loc.setName(l.getName());
                                    break;
                                }
                            }
                            newLocs.add(loc);
                        }
                        tour.setLocations(newLocs);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("tour" ,new Gson().toJson(tour));
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(AdminEditTourActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<BaseResponse> call, Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(AdminEditTourActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
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
