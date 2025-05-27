package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.utils.DataUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffLoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, roleSwitchButton;
    private LinearLayout formContainer;
    private ApiService apiService;
    private LocalDataService localDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_staff_login);

        TextView tvWelcome = findViewById(R.id.welcomeText);
        TextView tvSubtext = findViewById(R.id.loginText);
        DataUtils.applyGradientToText(tvWelcome);
        DataUtils.applyGradientToText(tvSubtext);

        View rootView = findViewById(R.id.rootContainer);
        formContainer = findViewById(R.id.formContainer);
        if (rootView == null || formContainer == null) {
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            int offset = Math.max(imeHeight, systemBar);

            formContainer.setTranslationY(-offset * 0.5f);

            return insets;
        });

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        roleSwitchButton = findViewById(R.id.roleSwitchButton);

        // Khởi tạo service
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);

        // Sự kiện bấm nút login
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (!username.isEmpty() && !password.isEmpty()) {
                sysLogin(username, password);
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            }
        });

        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(StaffLoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        roleSwitchButton.setOnClickListener(v -> {
            showRoleSwitchDialog();
        });
    }

    private void sysLogin(String username, String password) {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);

        apiService.sysLogin(data).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse res = response.body();
                    Toast.makeText(StaffLoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    if (res.getCode().equals(Code.SUCCESS.getCode())) {
                        Intent intent = new Intent(StaffLoginActivity.this, AdminMainActivity.class);
                        localDataService.saveUserInfo(res.getData());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(StaffLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(StaffLoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animateRoleSwitchAndNotify() {
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleBack = AnimationUtils.loadAnimation(this, R.anim.scale_back);

        roleSwitchButton.startAnimation(scaleUp);

        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                roleSwitchButton.startAnimation(scaleBack);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void showRoleSwitchDialog() {
        animateRoleSwitchAndNotify();
        new AlertDialog.Builder(this)
                .setMessage("Bạn có muốn chuyển sang giao diện người dùng không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    Intent intent = new Intent(StaffLoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
} 