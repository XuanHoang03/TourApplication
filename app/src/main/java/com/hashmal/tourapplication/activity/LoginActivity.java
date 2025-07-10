package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.admin.AdminMainActivity;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.hashmal.tourapplication.utils.DataUtils;


import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, txtGuestLogin, welcomeText, loginText, roleSwitchButton;
    private Button txtCreateAccount;
    private ApiService apiService;
    private LinearLayout formContainer;
    private LocalDataService localDataService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localDataService = LocalDataService.getInstance(this);
        if (Objects.nonNull(localDataService.getCurrentUser())) {
            UserDTO user = localDataService.getCurrentUser();
            if (user.getAccount().getRoleName().equals(RoleEnum.CUSTOMER.getRoleName())) {
                Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            } else {
                Intent homeIntent = new Intent(LoginActivity.this, AdminMainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    token = task.getResult();
                });

        TextView tvWelcome = findViewById(R.id.welcomeText);
        TextView tvSubtext = findViewById(R.id.loginText);
        DataUtils.applyGradientToText(tvWelcome);
        DataUtils.applyGradientToText(tvSubtext);

        View rootView = findViewById(R.id.rootContainer);
        formContainer = findViewById(R.id.formContainer);
        if (rootView == null || formContainer == null) {
            // Log error or throw exception
            return;
        }
        // Listen for window insets (e.g. when keyboard shows)
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            int offset = Math.max(imeHeight, systemBar);

            formContainer.setTranslationY(-offset * 0.5f); // Push layout up

            return insets;
        });

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtGuestLogin = findViewById(R.id.txtGuestLogin);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        roleSwitchButton = findViewById(R.id.roleSwitchButton);
        //TODO: mock for test
        edtUsername.setText("0123456789");
        edtPassword.setText("1");

        // Khởi tạo service
        apiService = ApiClient.getApiService();

        // Sự kiện bấm nút login
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (!username.isEmpty() && !password.isEmpty()) {
                btnLogin.setEnabled(false);
                login(username, password, token);

            } else {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        // Các hành động khác (tuỳ chỉnh theo app của bạn)
        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        txtGuestLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Guest login clicked", Toast.LENGTH_SHORT).show();
        });

        txtCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterStep1.class);
            startActivity(intent);
        });
        roleSwitchButton.setOnClickListener(v -> {
            showRoleSwitchDialog();
        });
    }

    private void login(String username, String password, String fcmToken) {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);

        apiService.login(data, fcmToken).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse res = response.body();
                    Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO: Chuyển màn hình / lưu token nếu có
                    if (res.getCode().equals(Code.SUCCESS.getCode())) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        localDataService.saveUserInfo(res.getData());
                        startActivity(intent);
                        finish();
                    }
                    if (res.getCode().equals(Code.ER0004.getCode())) {
                        Intent intent = new Intent(LoginActivity.this, VerifyAccountActivity.class);
                        intent.putExtra("registerUserId", (String) res.getData() );
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
                }
                btnLogin.setEnabled(true);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }
        });
    }

    private void animateRoleSwitchAndNotify() {
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation scaleBack = AnimationUtils.loadAnimation(this, R.anim.scale_back);

        roleSwitchButton.startAnimation(scaleUp);

        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                roleSwitchButton.startAnimation(scaleBack);

                // Hiển thị thông báo
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void showRoleSwitchDialog() {
        animateRoleSwitchAndNotify();
        new AlertDialog.Builder(this)
                .setMessage("Bạn có muốn chuyển sang giao diện nhân viên không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Chạy hiệu ứng
                    Intent staffLoginIntent = new Intent(LoginActivity.this, StaffLoginActivity.class);
                    startActivity(staffLoginIntent);
                    finish();
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}
