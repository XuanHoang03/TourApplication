package com.hashmal.tourapplication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;

import java.util.Calendar;

public class RegisterStep1 extends AppCompatActivity {
    private LinearLayout formContainer;
    private EditText edtBirthDate, edtFullName, edtPhoneNumber, edtAddress;
    private Spinner spnGender;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.register_step_1);

        View rootView = findViewById(R.id.rootRegisterContainer);
        formContainer = findViewById(R.id.registerContainer);

        if (rootView == null || formContainer == null) {
            // Log error or throw exception
            return;
        }
        // Listen for window insets (e.g. when keyboard shows)
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            int offset = Math.max(imeHeight, systemBar);

            formContainer.setTranslationY(-offset * 0.76f); // Push layout up

            return insets;
        });
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtAddress = findViewById(R.id.edtAddress);
        spnGender = findViewById(R.id.spinnerGender);
        edtBirthDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegisterStep1.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        edtBirthDate.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });
        nextBtn = findViewById(R.id.btnNext);
        nextBtn.setOnClickListener(v -> {
            String address = edtAddress.getText().toString();
            String birthDate = edtBirthDate.getText().toString();
            String phoneNumber = edtPhoneNumber.getText().toString();
            String fullName = edtFullName.getText().toString();
            String gender = spnGender.getSelectedItem().toString();

            // Validate
            if (!fullName.matches("^[\\p{L} .'-]{2,50}$")) {
                edtFullName.setError("Họ và tên không hợp lệ");
                edtFullName.requestFocus();
                return;
            }
            if (!phoneNumber.matches("^(0[3|5|7|8|9])+([0-9]{8})$")) {
                edtPhoneNumber.setError("Số điện thoại không hợp lệ");
                edtPhoneNumber.requestFocus();
                return;
            }
            if (address.length() < 5) {
                edtAddress.setError("Địa chỉ phải từ 5 ký tự trở lên");
                edtAddress.requestFocus();
                return;
            }
            if (birthDate.isEmpty()) {
                edtBirthDate.setError("Vui lòng chọn ngày sinh");
                edtBirthDate.requestFocus();
                return;
            }

            Intent intent = new Intent(RegisterStep1.this, RegisterStep2.class);
            intent.putExtra("fullName", fullName);
            intent.putExtra("phone", phoneNumber);
            intent.putExtra("address", address);
            intent.putExtra("birthDate", birthDate);
            intent.putExtra("gender", gender);
            startActivity(intent);
        });
    }
}