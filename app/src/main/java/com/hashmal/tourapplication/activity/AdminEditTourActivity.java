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

import java.util.List;

public class AdminEditTourActivity extends AppCompatActivity {
    private TourResponseDTO tour;
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
        LinearLayout layoutLocations = findViewById(R.id.layoutSelectedLocations);
        Button btnSave = findViewById(R.id.btnSaveTour);
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
        // TODO: Hiển thị danh sách địa điểm, cho phép thêm/xóa/sắp xếp lại
        // TODO: Cho phép đổi ảnh
        // TODO: Xử lý nút Lưu (gọi API cập nhật tour)
        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Đã lưu (demo)", Toast.LENGTH_SHORT).show();
            finish();
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
}
