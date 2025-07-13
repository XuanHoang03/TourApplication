package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtCurrentPassword;
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private Button btnChangePassword;
    private LocalDataService localDataService;
    private Button btnCancel;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        localDataService = LocalDataService.getInstance(this);
        // Initialize views
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnCancel = findViewById(R.id.btnCancel);

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Set click listeners
        btnChangePassword.setOnClickListener(v -> validateAndChangePassword());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void validateAndChangePassword() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
            edtCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            edtNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            edtNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu mới", Toast.LENGTH_SHORT).show();
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            edtConfirmPassword.requestFocus();
            return;
        }

        if (currentPassword.equals(newPassword)) {
            Toast.makeText(this, "Mật khẩu mới không được trùng với mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
            edtNewPassword.requestFocus();
            return;
        }

        // Call API to change password
        changePassword(currentPassword, newPassword);
    }

    private void changePassword(String currentPassword, String newPassword) {
        // Show loading state
        btnChangePassword.setEnabled(false);
        btnChangePassword.setText("Đang xử lý...");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("userId", localDataService.getCurrentUser().getAccount().getAccountId());
        requestBody.put("oldPassword", currentPassword);
        requestBody.put("newPassword", newPassword);

        // Assuming you have a changePassword endpoint in your API
        // You may need to adjust this based on your actual API structure
        apiService.changePassword(requestBody).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("Thay đổi mật khẩu");

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse res = response.body();
                    if (res.getCode().equals(Code.SUCCESS.getCode())) {
                        Toast.makeText(ChangePasswordActivity.this, 
                            "Thay đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                        
                        // Clear the form
                        edtCurrentPassword.setText("");
                        edtNewPassword.setText("");
                        edtConfirmPassword.setText("");
                        
                        // Return to previous activity
                        finish();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, 
                            res.getMessage() != null ? res.getMessage() : "Thay đổi mật khẩu thất bại", 
                            Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, 
                        "Thay đổi mật khẩu thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("Thay đổi mật khẩu");
                Toast.makeText(ChangePasswordActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Handle back button press
        super.onBackPressed();
        finish();
    }
} 