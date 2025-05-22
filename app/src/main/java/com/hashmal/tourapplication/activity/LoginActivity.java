package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.utils.DataUtils;


import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, txtGuestLogin, welcomeText, loginText;
    private Button txtCreateAccount;
    private ApiService apiService;
    private LinearLayout formContainer;
    private LocalDataService localDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);

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

        //TODO: mock for test
        edtUsername.setText("0123456789");
        edtPassword.setText("1");

        // Khởi tạo service
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(this);

        // Sự kiện bấm nút login
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (!username.isEmpty() && !password.isEmpty()) {
                login(username, password);

            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
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
    }

    private void login(String username, String password) {
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);

        apiService.login(data).enqueue(new Callback<BaseResponse>() {
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
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
