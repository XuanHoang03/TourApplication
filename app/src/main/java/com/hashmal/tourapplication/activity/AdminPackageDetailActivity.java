package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;

public class AdminPackageDetailActivity extends AppCompatActivity {
    private TourPackageDTO pkg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_package_detail);
        String pkgJson = getIntent().getStringExtra("package");
        pkg = new Gson().fromJson(pkgJson, TourPackageDTO.class);
        TextView tvName = findViewById(R.id.tvPackageName);
        TextView tvDesc = findViewById(R.id.tvPackageDescription);
        TextView tvPrice = findViewById(R.id.tvPackagePrice);
        TextView tvIsMain = findViewById(R.id.tvIsMain);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        tvName.setText(pkg.getPackageName());
        tvDesc.setText(pkg.getDescription());
        tvPrice.setText(pkg.getPrice() != null ? pkg.getPrice().toString() : "");
        tvIsMain.setText(pkg.isMain() ? "Gói chính" : "Gói phụ");
        Button btnEdit = findViewById(R.id.btnEditPackage);
        Button btnDelete = findViewById(R.id.btnDeletePackage);
        btnEdit.setOnClickListener(v -> Toast.makeText(this, "Chức năng Sửa (chưa làm)", Toast.LENGTH_SHORT).show());
        btnDelete.setOnClickListener(v -> Toast.makeText(this, "Chức năng Xóa (chưa làm)", Toast.LENGTH_SHORT).show());
    }
} 