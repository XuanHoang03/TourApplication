package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.entity.GenericKeyEvent;
import com.hashmal.tourapplication.entity.GenericTextWatcher;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.utils.DataUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAccountActivity extends AppCompatActivity {
    private LinearLayout formContainer;
    private EditText otp1 , otp2 , otp3, otp4;
    private ApiService apiService;

    private Button btnVerify;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.verify_register);

        TextView tvWelcome = findViewById(R.id.loginText);
        DataUtils.applyGradientToText(tvWelcome);

        View rootView = findViewById(R.id.rootVerifyContainer);
        formContainer = findViewById(R.id.verifyContainer);
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
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);

        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));

        otp2.setOnKeyListener(new GenericKeyEvent(otp2, otp1));
        otp3.setOnKeyListener(new GenericKeyEvent(otp3, otp2));
        otp4.setOnKeyListener(new GenericKeyEvent(otp4, otp3));
        apiService = ApiClient.getApiService();

        intent = getIntent();
        String userId = intent.getStringExtra("registerUserId");
        btnVerify = findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(v -> {
            String code = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
            Call<BaseResponse> call = apiService.verifyCode(code, userId);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        new AlertDialog.Builder(VerifyAccountActivity.this) // hoặc YourActivityName.this
                                .setTitle("Xác thực thành công")
                                .setMessage(response.body().getMessage())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    // Chuyển màn hình hoặc đóng dialog
                                    Intent intent = new Intent(VerifyAccountActivity.this, LoginActivity.class); // thay bằng activity cần chuyển
                                    startActivity(intent);
                                })
                                .show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Lỗi mạng hoặc server!", Toast.LENGTH_SHORT).show();
                    Log.e("VerifyAPI", "Error: " + t.getMessage());
                }
            });
        });
    }
}