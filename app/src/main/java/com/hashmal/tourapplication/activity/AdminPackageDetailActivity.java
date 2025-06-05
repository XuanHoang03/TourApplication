package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.UpdateTourPackageRequest;

import retrofit2.Call;
import retrofit2.Response;

public class AdminPackageDetailActivity extends AppCompatActivity {
    private TourPackageDTO pkg;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_package_detail);
        apiService = ApiClient.getApiService();
        String pkgJson = getIntent().getStringExtra("package");
        pkg = new Gson().fromJson(pkgJson, TourPackageDTO.class);


        TextView tvName = findViewById(R.id.tvPackageName);
        TextView tvDesc = findViewById(R.id.tvPackageDescription);
        TextView tvPrice = findViewById(R.id.tvPackagePrice);
        TextView tvIsMain = findViewById(R.id.tvIsMain);
        ImageButton btnBack = findViewById(R.id.btnBack);
        
        btnBack.setOnClickListener(v -> handleBackPressed());
        tvName.setText(pkg.getPackageName());
        tvDesc.setText(pkg.getDescription());
        tvPrice.setText(pkg.getPrice() != null ? pkg.getPrice().toString() : "");
        tvIsMain.setText(pkg.isMain() ? "Gói chính" : "Gói phụ");
        Button btnEdit = findViewById(R.id.btnEditPackage);
        Button btnDelete = findViewById(R.id.btnDeletePackage);
        btnEdit.setOnClickListener(v -> {
            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).create();
            android.view.LayoutInflater inflater = getLayoutInflater();
            android.view.View dialogView = inflater.inflate(R.layout.dialog_edit_package, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            com.google.android.material.textfield.TextInputEditText edtName = dialogView.findViewById(R.id.edtPackageName);
            com.google.android.material.textfield.TextInputEditText edtDesc = dialogView.findViewById(R.id.edtPackageDescription);
            com.google.android.material.textfield.TextInputEditText edtPrice = dialogView.findViewById(R.id.edtPackagePrice);
            android.widget.CheckBox cbIsMain = dialogView.findViewById(R.id.cbIsMain);
            android.widget.ImageButton btnBackDialog = dialogView.findViewById(R.id.btnBackDialog);
            btnBackDialog.setOnClickListener(v1 -> dialog.dismiss());
            edtName.setText(pkg.getPackageName());
            edtDesc.setText(pkg.getDescription());
            edtPrice.setText(pkg.getPrice() != null ? pkg.getPrice().toString() : "");
            cbIsMain.setChecked(pkg.isMain());
            Button btnSave = dialogView.findViewById(R.id.btnSavePackage);
            btnSave.setOnClickListener(v2 -> {
                String name = edtName.getText().toString().trim();
                String desc = edtDesc.getText().toString().trim();
                String priceStr = edtPrice.getText().toString().trim();
                boolean isMain = cbIsMain.isChecked();
                if (name.isEmpty() || priceStr.isEmpty()) {
                    edtName.setError(name.isEmpty() ? "Bắt buộc" : null);
                    edtPrice.setError(priceStr.isEmpty() ? "Bắt buộc" : null);
                    return;
                }
                Long price;
                try { price = Long.parseLong(priceStr); } catch (Exception e) { edtPrice.setError("Giá không hợp lệ"); return; }
                UpdateTourPackageRequest req = new UpdateTourPackageRequest(pkg.getId(), name, desc, price, null);
                btnSave.setEnabled(false);
                apiService.updateTourPackage(req).enqueue(new retrofit2.Callback<BaseResponse>() {
                    @Override public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        btnSave.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && Code.SUCCESS.getCode().equals(response.body().getCode())) {
                            android.widget.Toast.makeText(AdminPackageDetailActivity.this, "Cập nhật thành công", android.widget.Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            // Cập nhật lại UI
                            pkg.setPackageName(name);
                            pkg.setDescription(desc);
                            pkg.setPrice(price);
                            pkg.setMain(isMain);
                            reloadPackageUI();
                        } else {
                            android.widget.Toast.makeText(AdminPackageDetailActivity.this, "Cập nhật thất bại", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<BaseResponse> call, Throwable t) {
                        btnSave.setEnabled(true);
                        android.widget.Toast.makeText(AdminPackageDetailActivity.this, "Lỗi kết nối", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialog.show();
        });
        btnDelete.setOnClickListener(v -> Toast.makeText(this, "Chức năng Xóa (chưa làm)", Toast.LENGTH_SHORT).show());
    }

    private void handleBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("package", new Gson().toJson(pkg));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void reloadPackageUI() {
        TextView tvName = findViewById(R.id.tvPackageName);
        TextView tvDesc = findViewById(R.id.tvPackageDescription);
        TextView tvPrice = findViewById(R.id.tvPackagePrice);
        TextView tvIsMain = findViewById(R.id.tvIsMain);
        tvName.setText(pkg.getPackageName());
        tvDesc.setText(pkg.getDescription());
        tvPrice.setText(pkg.getPrice() != null ? pkg.getPrice().toString() : "");
        tvIsMain.setText(pkg.isMain() ? "Gói chính" : "Gói phụ");


    }
} 