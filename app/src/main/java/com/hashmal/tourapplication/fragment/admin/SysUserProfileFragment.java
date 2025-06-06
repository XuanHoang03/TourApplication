package com.hashmal.tourapplication.fragment.admin;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.ProfileActivity;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.hashmal.tourapplication.service.dto.UpdateProfileRequest;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.hashmal.tourapplication.utils.DataUtils;

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

public class SysUserProfileFragment extends Fragment {

    private LocalDataService localDataService;
    private ImageView profileImage;
    private TextInputEditText fullNameInput, emailInput, phoneInput, addressInput, dateOfBirthInput;
    private Button changePhotoButton, saveButton, editButton;

    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    private SysUserDTO user;

    private ApiService apiService;
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();

                    // Hiển thị ảnh
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .into(profileImage);

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sys_user_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileImage = view.findViewById(R.id.profileImage);
        fullNameInput = view.findViewById(R.id.fullNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        phoneInput = view.findViewById(R.id.phoneInput);
        addressInput = view.findViewById(R.id.addressInput);
        dateOfBirthInput = view.findViewById(R.id.dateOfBirthInput);
        changePhotoButton = view.findViewById(R.id.changePhotoButton);
        saveButton = view.findViewById(R.id.saveButton);
        editButton = view.findViewById(R.id.editButton);
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        apiService = ApiClient.getApiService();
        localDataService = LocalDataService.getInstance(requireContext());
        loadProfileData();

        editButton.setOnClickListener(v -> editButtonClicked());
        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });
        dateOfBirthInput.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> {
            validateInputs();
            saveUserProfile();
        });
    }

    private void saveUserProfile() {
        editButton.setEnabled(false);

        SysUserDTO user = localDataService.getSysUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
            return;
        }

        String dobStr = dateOfBirthInput.getText().toString().trim();
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
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

        apiService.updateSysProfile(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                saveButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null &&
                        Code.SUCCESS.getCode().equals(response.body().getCode())) {
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    saveButton.setVisibility(GONE);
                    editButton.setVisibility(VISIBLE);
                    editButton.setEnabled(true);
                    disableEditText();
                    localDataService.saveUserInfo(response.body().getData());
                    loadProfileData();
                } else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                saveButton.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        String currentDate = dateOfBirthInput.getText().toString();
        if (!currentDate.isEmpty()) {
            try {
                calendar.setTime(dateFormatter.parse(currentDate));
            } catch (Exception ignored) {
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
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

    private void loadProfileData() {
        user = localDataService.getSysUser();

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

    private void enableEditText() {
        fullNameInput.setEnabled(true);
        emailInput.setEnabled(true);
        phoneInput.setEnabled(false);
        addressInput.setEnabled(true);
        dateOfBirthInput.setEnabled(true);
    }

    private void disableEditText() {
        fullNameInput.setEnabled(false);
        emailInput.setEnabled(false);
        phoneInput.setEnabled(false);
        addressInput.setEnabled(false);
        dateOfBirthInput.setEnabled(false);
    }

    private void editButtonClicked() {
        enableEditText();
        editButton.setVisibility(GONE);
        saveButton.setVisibility(VISIBLE);
        saveButton.setEnabled(true);
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private void uploadAvatarFromUri(String profileId, Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile);
            RequestBody profileIdPart = RequestBody.create(MediaType.parse("text/plain"), profileId);

            apiService.uploadAvatar(filePart, profileIdPart)
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                                SysUserDTO newDTO =  user;
                                        newDTO.getProfile().setAvatarUrl((String) response.body().getData());
                                localDataService.saveUserInfo(newDTO);
                                loadProfileData();
                            } else {
                                Toast.makeText(requireContext(), "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Được cấp quyền
            Toast.makeText(requireContext(), "Đã cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_LONG).show();
        }
    }


} 