package com.hashmal.tourapplication.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.UserDTO;

public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private TextInputEditText fullNameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextInputEditText addressInput;
    private Button changePhotoButton;
    private Button saveButton;
    private LocalDataService localDataService;
    private Uri selectedImageUri;

//    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
//        new ActivityResultContracts.StartActivityForResult(),
//        result -> {
//            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
//                selectedImageUri = result.getData().getData();
//                // Load selected image using Glide
//                Glide.with(this)
//                    .load(selectedImageUri)
//                    .circleCrop()
//                    .into(profileImage);
//            }
//        }
//    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize LocalDataService
        localDataService = LocalDataService.getInstance(requireContext());

        // Initialize views
        initializeViews(view);

        // Load user data
        loadUserData();

        // Set up click listeners
//        setupClickListeners();
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        fullNameInput = view.findViewById(R.id.fullNameInput);
        emailInput = view.findViewById(R.id.emailInput);
        phoneInput = view.findViewById(R.id.phoneInput);
        addressInput = view.findViewById(R.id.addressInput);
        changePhotoButton = view.findViewById(R.id.changePhotoButton);
        saveButton = view.findViewById(R.id.saveButton);
    }

    private void loadUserData() {
        UserDTO currentUser = localDataService.getCurrentUser();
        if (currentUser != null) {
            fullNameInput.setText(currentUser.getProfile().getFullName());
            emailInput.setText(currentUser.getProfile().getEmail());
            phoneInput.setText(currentUser.getProfile().getPhoneNumber());
            addressInput.setText(currentUser.getProfile().getAddress());

            // Load profile image if exists
            if (currentUser.getProfile().getAvatarUrl() != null) {
                Glide.with(this)
                    .load(currentUser.getProfile().getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(profileImage);
            }
        }
    }

//    private void setupClickListeners() {
//        // Change photo button click
//        changePhotoButton.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            pickImage.launch(intent);
//        });
//
//        // Save button click
//        saveButton.setOnClickListener(v -> {
//            if (validateInputs()) {
//                saveUserProfile();
//            }
//        });
//    }
//
//    private boolean validateInputs() {
//        String fullName = fullNameInput.getText().toString().trim();
//        String email = emailInput.getText().toString().trim();
//        String phone = phoneInput.getText().toString().trim();
//        String address = addressInput.getText().toString().trim();
//
//        if (fullName.isEmpty()) {
//            fullNameInput.setError("Vui lòng nhập họ tên");
//            return false;
//        }
//
//        if (email.isEmpty()) {
//            emailInput.setError("Vui lòng nhập email");
//            return false;
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            emailInput.setError("Email không hợp lệ");
//            return false;
//        }
//
//        if (phone.isEmpty()) {
//            phoneInput.setError("Vui lòng nhập số điện thoại");
//            return false;
//        }
//
//        return true;
//    }
//
//    private void saveUserProfile() {
//        // TODO: Implement API call to update user profile
//        // For now, just show a success message
//        Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
//    }
} 