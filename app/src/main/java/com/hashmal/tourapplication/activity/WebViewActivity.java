package com.hashmal.tourapplication.activity;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hashmal.tourapplication.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.web_view);

        // Lấy URL thanh toán từ intent
        String paymentUrl = getIntent().getStringExtra("payment_url");
        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Toast.makeText(this, "URL thanh toán không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cấu hình WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Kiểm tra nếu URL chứa return URL của bạn
                String url = request.getUrl().toString();
                if (url.contains("tour-operav.asia/api/payment/vnpay-return")) {
                    // Xử lý kết quả thanh toán
                    handlePaymentResult(url);
                    return true;
                }
                return false;
            }
        });

        // Load URL thanh toán
        webView.loadUrl(paymentUrl);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(true);
                    finish();
                }
            }
        });

    }

    private void handlePaymentResult(String url) {
        // Phân tích URL để lấy các tham số kết quả thanh toán
        Uri uri = Uri.parse(url);
        String vnp_ResponseCode = uri.getQueryParameter("vnp_ResponseCode");

        if ("00".equals(vnp_ResponseCode)) {
            // Thanh toán thành công
            setResult(RESULT_OK);
        } else {
            // Thanh toán thất bại
            setResult(RESULT_CANCELED);
        }

        finish();
    }

}