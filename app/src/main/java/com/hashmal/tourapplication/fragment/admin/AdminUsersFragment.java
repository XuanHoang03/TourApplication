package com.hashmal.tourapplication.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.admin.AdminUsersAdapter;
import com.hashmal.tourapplication.dialog.CustomDialog;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.UserManagementDTO;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.hashmal.tourapplication.utils.DataUtils;
import com.hashmal.tourapplication.service.dto.UpdateUserByAdminRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private FloatingActionButton fabAddUser;
    private AdminUsersAdapter usersAdapter;
    private ApiService apiService;
    private List<UserDTO> allUsers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_users, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvUsers = view.findViewById(R.id.rvUsers);
        SearchView searchView = view.findViewById(R.id.searchViewUser);
//        fabAddUser = view.findViewById(R.id.fabAddUser);

        // Setup RecyclerView
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        usersAdapter = new AdminUsersAdapter(requireContext());
        rvUsers.setAdapter(usersAdapter);

        // Initialize API service
        apiService = ApiClient.getApiService();

        // Setup click listeners
//        fabAddUser.setOnClickListener(v -> {
//            // TODO: Show add user dialog
//            Toast.makeText(requireContext(), "Add user clicked", Toast.LENGTH_SHORT).show();
//        });

        usersAdapter.setOnUserClickListener(user -> showUserInfoDialog(user));
        usersAdapter.setOnUserActionListener(new AdminUsersAdapter.OnUserActionListener() {
            @Override
            public void onEditUser(UserDTO user) {
                showEditUserDialog(user);
            }

            @Override
            public void onDeleteUser(UserDTO user) {
                // Xử lý xoá user nếu cần
                String action = "";
                if (user.getAccount().getStatus() == 1) {
                    action ="vô hiệu hóa";
                } else if (user.getAccount().getStatus() == 0) {
                    new CustomDialog.Builder(requireContext())
                        .setTitle("Thông báo")
                        .setMessage("Tài khoản này chưa được kích hoạt hoặc đang chờ xác thực. Không thể thực hiện thao tác này.")
                        .setPositiveButton("Tôi đã hiểu", dialog -> dialog.dismiss())
                        .setSingleButtonMode(true)
                        .show();
                    return;
                } else {
                    action = "kích hoạt";
                }

                new CustomDialog.Builder(requireContext())
                        .setTitle("Thông báo")
                        .setMessage("Bạn muốn " + action + " tài khoản này?" )
                        .setPositiveButton("Đúng", dialog -> {
                            UpdateUserByAdminRequest req = new UpdateUserByAdminRequest();
                            req.setAccountId(user.getAccount().getAccountId());
                            req.setStatus(user.getAccount().getStatus() == 1 ? -1 : 1 );
                            apiService.updateUserByAdmin(req).enqueue(new Callback<BaseResponse>() {
                                @Override
                                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                    if (response.isSuccessful() && response.body() != null && Code.SUCCESS.getCode().equals(response.body().getCode())) {
                                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        loadUsers();
                                    } else {
                                        Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<BaseResponse> call, Throwable t) {
                                    Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Không", dialog -> {
                            dialog.dismiss();
                        })
                        .setSingleButtonMode(false)
                        .show();
            }
        });

        // Search logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        apiService.getUserManagement().enqueue(new Callback<UserManagementDTO>() {
            @Override
            public void onResponse(Call<UserManagementDTO> call, Response<UserManagementDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserManagementDTO data = response.body();
                    allUsers = data.getUsers();
                    usersAdapter.updateUsers(allUsers);
                } else {
                    Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserManagementDTO> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        if (allUsers == null) return;
        String lower = query.toLowerCase();
        List<UserDTO> filtered = new ArrayList<>();
        for (UserDTO user : allUsers) {
            if (user.getProfile().getFullName().toLowerCase().contains(lower)
                    || user.getProfile().getEmail().toLowerCase().contains(lower)
                    || user.getAccount().getUsername().toLowerCase().contains(lower)
                    || user.getAccount().getAccountId().trim().contains(lower)) {
                filtered.add(user);
            }
        }
        usersAdapter.updateUsers(filtered);
    }

    private void showUserInfoDialog(UserDTO user) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_user_info, null, false);
        ImageView imgAvatar = dialogView.findViewById(R.id.imgAvatar);
        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvUsername = dialogView.findViewById(R.id.tvUsername);
        TextView tvEmail = dialogView.findViewById(R.id.tvEmail);
        TextView tvPhone = dialogView.findViewById(R.id.tvPhone);
        TextView tvAddress = dialogView.findViewById(R.id.tvAddress);
        TextView tvRole = dialogView.findViewById(R.id.tvRole);
        TextView tvGender = dialogView.findViewById(R.id.tvGender);
        TextView tvDob = dialogView.findViewById(R.id.tvDob);
        TextView tvStatus = dialogView.findViewById(R.id.tvStatus);

        tvName.setText(user.getProfile().getFullName());
        tvUsername.setText("Họ tên: " + user.getAccount().getUsername());
        tvEmail.setText("Email: " + user.getProfile().getEmail());
        tvPhone.setText("Số điện thoại: " + user.getProfile().getPhoneNumber());
        tvAddress.setText("Địa chỉ: " + (user.getProfile().getAddress() != null ? user.getProfile().getAddress() : ""));
        tvRole.setText("Vai trò: " + (user.getAccount().getRoleName() != null ? user.getAccount().getRoleName() : ""));
        Integer gender = user.getProfile().getGender();
        String genderStr = "";
        if (gender != null) {
            if (gender == 1) genderStr = "Nam";
            else if (gender == 0) genderStr = "Nữ";
            else genderStr = "Khác";
        }
        tvGender.setText("Giới tính: " + genderStr);
        tvDob.setText("Ngày sinh: " + (user.getProfile().getDob() != null ? DataUtils.parseDateOfBirth( user.getProfile().getDob() ): ""));

        Integer status = user.getAccount().getStatus();
        if (status != null && status == 1) {
            tvStatus.setText("Đang hoạt động");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (status != null && status == -1) {
            tvStatus.setText("Đã vô hiệu hóa");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvStatus.setText("Chưa kích hoạt");
            tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        String avatarUrl = user.getProfile().getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_profile);
        }

        final com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showEditUserDialog(UserDTO user) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_user, null, false);
        EditText edtFullName = dialogView.findViewById(R.id.edtFullName);
        EditText edtUsername = dialogView.findViewById(R.id.edtUsername);
        EditText edtPassword = dialogView.findViewById(R.id.edtPassword);
        ImageButton btnTogglePassword = dialogView.findViewById(R.id.btnTogglePassword);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtRole = dialogView.findViewById(R.id.edtRole);
        Spinner spinnerGender = dialogView.findViewById(R.id.spinnerGender);
        EditText edtDob = dialogView.findViewById(R.id.edtDob);
        MaterialTextView edtStatus = dialogView.findViewById(R.id.edtStatus);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Set data
        edtFullName.setText(user.getProfile().getFullName());
        edtUsername.setText(user.getAccount().getUsername());
        edtPassword.setText(user.getAccount().getPassword());
        edtEmail.setText(user.getProfile().getEmail());
        edtPhone.setText(user.getProfile().getPhoneNumber());
        edtAddress.setText(user.getProfile().getAddress());
        edtRole.setText(user.getAccount().getRoleName());
        Integer gender = user.getProfile().getGender();
        // Gender spinner setup
        String[] genderOptions = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        // Set spinner selection
        int genderIndex = 2; // Default Other
        if (gender != null) {
            if (gender == 1) genderIndex = 0;
            else if (gender == 0) genderIndex = 1;
        }
        spinnerGender.setSelection(genderIndex);
        edtDob.setText(DataUtils.parseDateOfBirth(user.getProfile().getDob()));
        Integer status = user.getAccount().getStatus();
        String statusStr = "";
        if (status != null && status == 1) statusStr = "Đang hoạt động";
        else if (status != null && status == -1) statusStr = "Đã vô hiệu hóa";
        else statusStr = "Chưa kích hoạt";
        edtStatus.setText(statusStr);

        // Password show/hide
        btnTogglePassword.setOnClickListener(v -> {
            if (edtPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                edtPassword.setSelection(edtPassword.getText().length());
            } else {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edtPassword.setSelection(edtPassword.getText().length());
            }
        });

        final com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);
        dialog.show();

        btnSave.setOnClickListener(v -> {
            // Validate
            String fullName = edtFullName.getText().toString().trim();
            String password = edtPassword.getText().toString();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String role = edtRole.getText().toString().trim();
            String dobStr = edtDob.getText().toString().trim();
            LocalDate dob = null;
            try {
                dob = LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                Toast.makeText(requireContext(),
                        "Ngày sinh không hợp lệ",
                        Toast.LENGTH_SHORT).show();
                dob = LocalDate.now();
            }

            // Gender parse
            int genderVal = 2;
            String genderStr = spinnerGender.getSelectedItem().toString();
            if (genderStr.equalsIgnoreCase("Nam")) genderVal = 1;
            else if (genderStr.equalsIgnoreCase("Nữ")) genderVal = 0;

            UpdateUserByAdminRequest req = new UpdateUserByAdminRequest();
            req.setAccountId(user.getAccount().getAccountId());
            req.setUsername(user.getAccount().getUsername());
            req.setPassword(password);
            req.setStatus(user.getAccount().getStatus());
            req.setFullName(fullName);
            req.setEmail(email);
            req.setAddress(address);
            req.setAvatarUrl(user.getProfile().getAvatarUrl());
            req.setGender(genderVal);
            req.setDob(dob.toString());

            btnSave.setEnabled(false);
            apiService.updateUserByAdmin(req).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && Code.SUCCESS.getCode().equals(response.body().getCode())) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadUsers();
                    } else {
                        Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
} 