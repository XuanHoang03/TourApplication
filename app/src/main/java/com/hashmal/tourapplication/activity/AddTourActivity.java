package com.hashmal.tourapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.CreateTourRequest;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.utils.DataUtils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTourActivity extends AppCompatActivity {
    private TextInputEditText edtTourName, edtTourType, edtTourDescription, edtNumberOfPeople, edtDuration, edtStart, edtEnd;
    private Button btnPickImage, btnPickLocations, btnSaveTour;
    private ImageView imgThumbnailPreview;
    private TextView tvSelectedLocations;
    private LinearLayout layoutSelectedLocations;
    private Uri selectedImageUri = null;
    private List<YourTourDTO.Location> allLocations = new ArrayList<>();
    private List<CreateTourRequest.VisitOrderDTO> selectedLocations = new ArrayList<>();
    private ApiService apiService;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null && imgThumbnailPreview != null) {
                        imgThumbnailPreview.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);
        apiService = ApiClient.getApiService();
        edtTourName = findViewById(R.id.edtTourName);
        edtTourType = findViewById(R.id.edtTourType);
        edtTourDescription = findViewById(R.id.edtTourDescription);
        edtNumberOfPeople = findViewById(R.id.edtNumberOfPeople);
        edtDuration = findViewById(R.id.edtDuration);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        btnPickImage = findViewById(R.id.btnPickImage);
        imgThumbnailPreview = findViewById(R.id.imgThumbnailPreview);
        btnPickLocations = findViewById(R.id.btnPickLocations);
        tvSelectedLocations = findViewById(R.id.tvSelectedLocations);
        layoutSelectedLocations = findViewById(R.id.layoutSelectedLocations);
        btnSaveTour = findViewById(R.id.btnSaveTour);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });
        btnPickLocations.setOnClickListener(v -> showPickLocationsDialog());
        btnSaveTour.setOnClickListener(v -> saveTour());
        btnBack.setOnClickListener(v -> finish());

        edtStart.setOnClickListener(v -> showTimePicker(edtStart));
        edtEnd.setOnClickListener(v -> showTimePicker(edtEnd));
    }

    private void showPickLocationsDialog() {
        if (allLocations.isEmpty()) {
            apiService.getAllLocations().enqueue(new Callback<List<YourTourDTO.Location>>() {
                @Override
                public void onResponse(Call<List<YourTourDTO.Location>> call, Response<List<YourTourDTO.Location>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        allLocations = response.body();
                        showAddLocationDialog();
                    } else {
                        Toast.makeText(AddTourActivity.this, "Không tải được danh sách địa điểm", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<YourTourDTO.Location>> call, Throwable t) {
                    Toast.makeText(AddTourActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showAddLocationDialog();
        }
    }

    private void showAddLocationDialog() {
        List<YourTourDTO.Location> available = new ArrayList<>();
        for (YourTourDTO.Location loc : allLocations) {
            boolean already = false;
            for (CreateTourRequest.VisitOrderDTO dto : selectedLocations) {
                if (dto.getLocationId() != null && dto.getLocationId().equals(loc.getId())) {
                    already = true;
                    break;
                }
            }
            if (!already) available.add(loc);
        }
        if (available.isEmpty()) {
            Toast.makeText(this, "Đã chọn hết địa điểm!", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[available.size()];
        for (int i = 0; i < available.size(); i++) names[i] = available.get(i).getName();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn địa điểm để thêm")
                .setItems(names, (dialog, which) -> {
                    YourTourDTO.Location loc = available.get(which);
                    selectedLocations.add(new CreateTourRequest.VisitOrderDTO(loc.getId(), selectedLocations.size() + 1));
                    updateSelectedLocationsText();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateSelectedLocationsText() {
        layoutSelectedLocations.removeAllViews();
        if (selectedLocations.isEmpty()) {
            tvSelectedLocations.setText("Chưa chọn địa điểm nào");
            return;
        }
        tvSelectedLocations.setText("");
        for (int i = 0; i < selectedLocations.size(); i++) {
            final int index = i;
            Long id = selectedLocations.get(i).getLocationId();
            String name = "";
            for (YourTourDTO.Location loc : allLocations) {
                if (loc.getId() == id) {
                    name = loc.getName();
                    break;
                }
            }
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            TextView tv = new TextView(this);
            tv.setText((i + 1) + ". " + name);
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            Button btnRemove = new Button(this);
            btnRemove.setText("X");
            btnRemove.setTextSize(12);
            btnRemove.setPadding(16, 0, 16, 0);
            btnRemove.setOnClickListener(v -> {
                selectedLocations.remove(index);
                updateSelectedLocationsText();
            });
            row.addView(tv);
            row.addView(btnRemove);
            layoutSelectedLocations.addView(row);
        }
    }

    private void showTimePicker(TextInputEditText target) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);
        android.app.TimePickerDialog timePicker = new android.app.TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String timeStr = String.format("%02d:%02d", hourOfDay, minute1);
            target.setText(timeStr);
        }, hour, minute, true);
        timePicker.show();
    }

    private void saveTour() {
        String name = edtTourName.getText() != null ? edtTourName.getText().toString().trim() : "";
        String type = edtTourType.getText() != null ? edtTourType.getText().toString().trim() : "";
        String desc = edtTourDescription.getText() != null ? edtTourDescription.getText().toString().trim() : "";
        String numStr = edtNumberOfPeople.getText() != null ? edtNumberOfPeople.getText().toString().trim() : "";
        String duration = edtDuration.getText() != null ? edtDuration.getText().toString().trim() : "";
        String start = edtStart.getText() != null ? edtStart.getText().toString().trim() : "";
        String end = edtEnd.getText() != null ? edtEnd.getText().toString().trim() : "";
        String startApi = start.replace(":", "_");
        String endApi = end.replace(":", "_");
        if (name.isEmpty() || type.isEmpty() || numStr.isEmpty() || duration.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer numPeople = null;
        try {
            numPeople = Integer.parseInt(numStr);
        } catch (Exception e) {
        }
        if (numPeople == null) {
            Toast.makeText(this, "Số người không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn 1 ảnh cho Tour", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedLocations.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 địa điểm", Toast.LENGTH_SHORT).show();
            return;
        }
        btnSaveTour.setEnabled(false);
        uploadImageAndCreateTour(name, type, desc, numPeople, duration, startApi, endApi);
    }

    private void uploadImageAndCreateTour(String name, String type, String desc, int numPeople, String duration, String start, String end) {
        try {
            File file = createFileFromUri(selectedImageUri);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
            apiService.uploadImage(body).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode().equals(Code.SUCCESS.getCode())) {
                        String imageUrl = DataUtils.buildFullFilePath(String.valueOf(response.body().getData()));
                        createTour(name, type, desc, numPeople, duration, imageUrl, start, end);
                    } else {
                        btnSaveTour.setEnabled(true);
                        Toast.makeText(AddTourActivity.this, "Upload ảnh thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    btnSaveTour.setEnabled(true);
                    Toast.makeText(AddTourActivity.this, "Lỗi upload ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            btnSaveTour.setEnabled(true);
            Toast.makeText(this, "Lỗi file ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File file = new File(getCacheDir(), "upload_image.jpg");
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buf = new byte[4096];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }

    private void createTour(String name, String type, String desc, int numPeople, String duration, String imageUrl, String start, String end) {
        CreateTourRequest req = new CreateTourRequest(name, type, desc, numPeople, duration, imageUrl, selectedLocations, start, end);
        apiService.createTour(req).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                btnSaveTour.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getCode().equals(Code.SUCCESS.getCode())) {
                    Toast.makeText(AddTourActivity.this, "Tạo tour thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddTourActivity.this, "Tạo tour thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                btnSaveTour.setEnabled(true);
                Toast.makeText(AddTourActivity.this, "Lỗi tạo tour: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 