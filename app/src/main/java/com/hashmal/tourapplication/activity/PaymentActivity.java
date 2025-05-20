package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.PaymentRequest;
import com.hashmal.tourapplication.service.dto.PaymentResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private EditText etAmount;
    private EditText etOrderInfo;
    private Button btnPay;
    private ApiService apiService;


    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        apiService = ApiClient.getApiService();

        etAmount = findViewById(R.id.et_amount);
        etOrderInfo = findViewById(R.id.et_order_info);
        btnPay = findViewById(R.id.btn_pay);

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        try {
            long amount = Long.parseLong(etAmount.getText().toString().trim());
            String orderInfo = etOrderInfo.getText().toString().trim();

            if (amount <= 0) {
                Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (orderInfo.isEmpty()) {
                Toast.makeText(this, "Thông tin đơn hàng không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            PaymentRequest paymentRequest = new PaymentRequest(amount, orderInfo);
            Call<PaymentResponse> call = apiService.createPayment(paymentRequest);

            call.enqueue(new Callback<PaymentResponse>() {
                @Override
                public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String paymentUrl = response.body().getPaymentUrl();
                        openVNPayWebView(paymentUrl);
                    } else {
                        Toast.makeText(PaymentActivity.this, "Không thể tạo thanh toán", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PaymentResponse> call, Throwable t) {
                    Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void openVNPayWebView(String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("payment_url", url);
        paymentLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                // Xử lý kết quả thanh toán thành công
                Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            } else {
                // Xử lý kết quả thanh toán thất bại
                Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy", Toast.LENGTH_SHORT).show();
            }
        }
    }
}