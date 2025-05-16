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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.RegisterUserDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterStep2 extends AppCompatActivity {
    private LinearLayout formContainer;

    private EditText edtEmail, edtPassword, edtRetypePassword;
    private Button nextBtn;
    private Intent intent;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getApiService();

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_register_step2);

        View rootView = findViewById(R.id.rootRegisterContainer);
        formContainer = findViewById(R.id.registerContainer);

        if (rootView == null || formContainer == null) {
            // Log error or throw exception
            return;
        }

        intent = getIntent();
        // Listen for window insets (e.g. when keyboard shows)
//        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
//            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
//            int systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
//
//            int offset = Math.max(imeHeight, systemBar);
//
//            formContainer.setTranslationY(-offset * 0.4f); // Push layout up
//
//            return insets;
//        });

        nextBtn = findViewById(R.id.btnNext);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRetypePassword = findViewById(R.id.edtRetypePassword);

        nextBtn.setOnClickListener(v -> {
            String fullName = intent.getStringExtra("fullName");
            String phone = intent.getStringExtra("phone");
            String address = intent.getStringExtra("address");
            String birthDate = intent.getStringExtra("birthDate");
            String gender = intent.getStringExtra("gender");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDateTime birthDateVal = LocalDate.parse(birthDate, formatter).atStartOfDay();
            Integer genderVal = (gender.equals( "Male" ) ? 1 : 0);
            String email = edtEmail.getText().toString();
            String password= edtPassword.getText().toString();

            Map<String, Object> req = new HashMap<>();
            req.put("fullName", fullName);
            req.put("phoneNumber", phone);
            req.put("dob", birthDateVal.toString()); // "yyyy-MM-dd'T'HH:mm:ss"
            req.put("email", email);
            req.put("password", password);
            req.put("gender", genderVal);
            req.put("address", address);


            apiService.registerUser(req).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            BaseResponse resp = response.body();
                            Toast.makeText(RegisterStep2.this, resp.getMessage(), Toast.LENGTH_SHORT).show();

                            if (Code.SUCCESS.getCode().equals(resp.getCode())) {
                                String userId = (String) resp.getData();
                                Intent intent = new Intent(RegisterStep2.this, VerifyAccountActivity.class);
                                intent.putExtra("registerUserId", userId );
                                startActivity(intent);
                            }
                        } else {
                            Log.e("API", "Response failed: " + response.errorBody().string());
                            Toast.makeText(RegisterStep2.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("API", "Exception in response: " + e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Log.e("API", "Call failed: " + t.getMessage(), t);
                    Toast.makeText(RegisterStep2.this, "Register Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


}