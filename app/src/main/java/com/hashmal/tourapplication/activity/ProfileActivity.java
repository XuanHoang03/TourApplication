package com.hashmal.tourapplication.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.UpdateProfileRequest;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage, btnBack;
    private TextInputEditText fullNameInput, emailInput, phoneInput, addressInput, dateOfBirthInput;
    private Button changePhotoButton, saveButton, editButton;

    private LocalDataService localDataService;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();

                    // Hiển thị ảnh


                    // Upload ảnh lên server
                    String imagePath = getRealPathFromURI(selectedImageUri);
//                    if (imagePath != null) {
//                        File imageFile = new File(imagePath);
                        String profileId = localDataService.getCurrentUser().getProfile().getProfileId(); // hoặc userId
                        uploadAvatarFromUri(profileId, selectedImageUri);
//                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        localDataService = LocalDataService.getInstance(this);

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        dateOfBirthInput = findViewById(R.id.dateOfBirthInput);
        changePhotoButton = findViewById(R.id.changePhotoButton);
        saveButton = findViewById(R.id.saveButton);
        editButton = findViewById(R.id.updateButton);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void loadUserData() {
        UserDTO user = localDataService.getCurrentUser();
        if (user == null) return;

        fullNameInput.setText(user.getProfile().getFullName());
        emailInput.setText(user.getProfile().getEmail());
        phoneInput.setText(user.getProfile().getPhoneNumber());
        addressInput.setText(user.getProfile().getAddress());

        if (user.getProfile().getDob() != null) {
            dateOfBirthInput.setText(DataUtils.parseDateOfBirth(user.getProfile().getDob()));
        }

        if (user.getProfile().getAvatarUrl() != null) {
            Glide.with(this)
                    .load(user.getProfile().getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(profileImage);
        }
    }

    private void setupClickListeners() {
        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });

        dateOfBirthInput.setOnClickListener(v -> showDatePicker());

        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                saveUserProfile();
            }
        });

        editButton.setOnClickListener(v-> {
            editButton.setVisibility(GONE);
            saveButton.setVisibility(VISIBLE);
            saveButton.setEnabled(true);

            fullNameInput.setEnabled(true);
            emailInput.setEnabled(true);
            dateOfBirthInput.setEnabled(true);
            addressInput.setEnabled(true);
        });
    }

    private void showDatePicker() {
        String currentDate = dateOfBirthInput.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                calendar.setTime(dateFormatter.parse(currentDate));
            } catch (Exception ignored) {}
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    dateOfBirthInput.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean validateInputs() {
        if (fullNameInput.getText().toString().trim().isEmpty()) {
            fullNameInput.setError("Vui lòng nhập họ tên");
            return false;
        }
        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Vui lòng nhập email");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
            emailInput.setError("Email không hợp lệ");
            return false;
        }
        if (dateOfBirthInput.getText().toString().trim().isEmpty()) {
            dateOfBirthInput.setError("Vui lòng chọn ngày sinh");
            return false;
        }
        return true;
    }

    private void saveUserProfile() {
        saveButton.setEnabled(false);

        UserDTO user = localDataService.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
            editButton.setEnabled(true);
            return;
        }

        String dobStr = dateOfBirthInput.getText().toString().trim();
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            Toast.makeText(this, "Ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setUserId(user.getAccount().getAccountId());
        request.setFullName(fullNameInput.getText().toString().trim());
        request.setEmail(emailInput.getText().toString().trim());
        request.setPhoneNumber(phoneInput.getText().toString().trim());
        request.setAddress(addressInput.getText().toString().trim());
        request.setdOB(dob.toString());

        ApiClient.getApiService().updateProfile(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                saveButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null &&
                        Code.SUCCESS.getCode().equals(response.body().getCode())) {
                    fullNameInput.setEnabled(false);
                    emailInput.setEnabled(false);
                    dateOfBirthInput.setEnabled(false);
                    addressInput.setEnabled(false);
                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    localDataService.saveUserInfo(response.body().getData());
                    saveButton.setVisibility(GONE);
                    editButton.setVisibility(VISIBLE);
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                saveButton.setEnabled(true);
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatarFromUri(String profileId, Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile);
            RequestBody profileIdPart = RequestBody.create(MediaType.parse("text/plain"), profileId);

            ApiClient.getApiService().uploadAvatar(filePart, profileIdPart)
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                                Call<UserDTO> call2 = ApiClient.getApiService().getFullUserInformationById(localDataService.getCurrentUser().getAccount().getAccountId());
                                call2.enqueue(new Callback<UserDTO>() {
                                    @Override
                                    public void onResponse(Call<UserDTO> call2, Response<UserDTO> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            UserDTO userDTO = response.body();
                                            localDataService.saveUserInfo(userDTO);
                                            Glide.with(ProfileActivity.this)
                                                    .load(uri)
                                                    .circleCrop()
                                                    .into(profileImage);
                                            loadUserData();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<UserDTO> call2, Throwable t) {
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            Toast.makeText(ProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Được cấp quyền
            Toast.makeText(this, "Đã cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_LONG).show();
        }
    }

}
