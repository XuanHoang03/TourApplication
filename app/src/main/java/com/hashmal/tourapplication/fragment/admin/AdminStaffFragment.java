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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.admin.AdminSysUsersAdapter;
import com.hashmal.tourapplication.adapter.admin.AdminUsersAdapter;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.StaffManagementDTO;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.hashmal.tourapplication.service.dto.UpdateUserByAdminRequest;
import com.hashmal.tourapplication.service.dto.UserManagementDTO;
import com.hashmal.tourapplication.utils.DataUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class AdminStaffFragment extends Fragment {
    private RecyclerView rvStaffs;
    private AdminSysUsersAdapter staffsAdapter;
    private ApiService apiService;
    private List<SysUserDTO> allStaffs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_staffs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvStaffs = view.findViewById(R.id.rvStaffs);
        SearchView searchView = view.findViewById(R.id.searchViewStaff);
        FloatingActionButton fabAddStaff = view.findViewById(R.id.fabAddStaff);
        rvStaffs.setLayoutManager(new LinearLayoutManager(requireContext()));
        staffsAdapter = new AdminSysUsersAdapter(requireContext());
        rvStaffs.setAdapter(staffsAdapter);
        apiService = ApiClient.getApiService();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterStaffs(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterStaffs(newText);
                return true;
            }
        });
        staffsAdapter.setOnSysUserClickListener(this::showStaffInfoDialog);
        staffsAdapter.setOnSysUserActionListener(new AdminSysUsersAdapter.OnSysUserActionListener() {
            @Override
            public void onEditSysUser(SysUserDTO user) {
                showEditStaffDialog(user);
            }
            @Override
            public void onDeleteSysUser(SysUserDTO user) {
                showDeleteStaffDialog(user);
            }
        });
        fabAddStaff.setOnClickListener(v -> showAddStaffDialog());
        loadStaffs();
    }

    private void loadStaffs() {
        apiService.getStaffManagement().enqueue(new Callback<UserManagementDTO>() {
            @Override
            public void onResponse(Call<UserManagementDTO> call, Response<UserManagementDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserManagementDTO data = response.body();
                    allStaffs = data.getSysUser();
                    staffsAdapter.updateSysUsers(allStaffs);
                } else {
                    Toast.makeText(requireContext(), "Failed to load staffs", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserManagementDTO> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterStaffs(String query) {
        if (allStaffs == null) return;
        String lower = query.toLowerCase();
        List<SysUserDTO> filtered = new ArrayList<>();
        for (SysUserDTO staff : allStaffs) {
            if (staff.getProfile().getFullName().toLowerCase().contains(lower)
                || staff.getProfile().getEmail().toLowerCase().contains(lower)
                || staff.getAccount().getUsername().toLowerCase().contains(lower)) {
                filtered.add(staff);
            }
        }
        staffsAdapter.updateSysUsers(filtered);
    }

    private void showStaffInfoDialog(SysUserDTO staff) {
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

        tvName.setText(staff.getProfile().getFullName());
        tvUsername.setText("Họ tên: " + staff.getAccount().getUsername());
        tvEmail.setText("Email: " + staff.getProfile().getEmail());
        tvPhone.setText("Số điện thoại: " + staff.getProfile().getPhoneNumber());
        tvAddress.setText("Thường trú: " + (staff.getProfile().getAddress() != null ? staff.getProfile().getAddress() : ""));
        tvRole.setText("Chức vụ: " + (staff.getAccount().getRoleName() != null ? staff.getAccount().getRoleName() : ""));
        Integer gender = staff.getProfile().getGender();
        String genderStr = "";
        if (gender != null) {
            if (gender == 1) genderStr = "Nam";
            else if (gender == 0) genderStr = "Nữ";
            else genderStr = "Khác";
        }
        tvGender.setText("Giới tính: " + genderStr);
        tvDob.setText("Ngày sinh: " + (staff.getProfile().getDob() != null ? DataUtils.parseDateOfBirth( staff.getProfile().getDob()) : ""));

        Integer status = staff.getAccount().getStatus();
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
        String avatarUrl = staff.getProfile().getAvatarUrl();
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

    private void showEditStaffDialog(SysUserDTO staff) {
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
        edtFullName.setText(staff.getProfile().getFullName());
        edtUsername.setText(staff.getAccount().getUsername());
        edtPassword.setText(staff.getAccount().getPassword());
        edtEmail.setText(staff.getProfile().getEmail());
        edtPhone.setText(staff.getProfile().getPhoneNumber());
        edtAddress.setText(staff.getProfile().getAddress());
        edtRole.setText(staff.getAccount().getRoleName());
        Integer gender = staff.getProfile().getGender();
        // Gender spinner setup
        String[] genderOptions = {"Nam", "Nữ", ""};
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
        edtDob.setText(DataUtils.parseDateOfBirth(staff.getProfile().getDob()));
        Integer status = staff.getAccount().getStatus();
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
            java.time.LocalDate dob = null;
            try {
                dob = java.time.LocalDate.parse(dobStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                Toast.makeText(requireContext(),
                        "Ngày sinh không hợp lệ",
                        Toast.LENGTH_SHORT).show();
                dob = java.time.LocalDate.now();
            }

            // Gender parse
            int genderVal = 2;
            String genderStr = spinnerGender.getSelectedItem().toString();
            if (genderStr.equalsIgnoreCase("Nam")) genderVal = 1;
            else if (genderStr.equalsIgnoreCase("Nữ")) genderVal = 0;

            UpdateUserByAdminRequest req = new UpdateUserByAdminRequest();
            req.setAccountId(staff.getAccount().getAccountId());
            req.setUsername(staff.getAccount().getUsername());
            req.setPassword(password);
            req.setStatus(staff.getAccount().getStatus());
            req.setFullName(fullName);
            req.setEmail(email);
            req.setAddress(address);
            req.setAvatarUrl(staff.getProfile().getAvatarUrl());
            req.setGender(genderVal);
            req.setDob(dob.toString());

            btnSave.setEnabled(false);
            apiService.updateStaffByAdmin(req).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && Code.SUCCESS.getCode().equals(response.body().getCode())) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadStaffs();
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

    private void showAddStaffDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_staff, null, false);
        com.google.android.material.textfield.TextInputEditText edtFullName = dialogView.findViewById(R.id.edtFullName);
        com.google.android.material.textfield.TextInputEditText edtUsername = dialogView.findViewById(R.id.edtUsername);
        com.google.android.material.textfield.TextInputEditText edtPassword = dialogView.findViewById(R.id.edtPassword);
        com.google.android.material.textfield.TextInputEditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        com.google.android.material.textfield.TextInputEditText edtPhone = dialogView.findViewById(R.id.edtPhone);
        com.google.android.material.textfield.TextInputEditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinnerRole);
        Spinner spinnerGender = dialogView.findViewById(R.id.spinnerGender);
        com.google.android.material.textfield.TextInputEditText edtDob = dialogView.findViewById(R.id.edtDob);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Role spinner setup
        String[] roleOptions = {RoleEnum.TOUR_GUIDE.name(), RoleEnum.TOUR_OPERATOR.name(), ""};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roleOptions);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);
        spinnerRole.setSelection(0);

        // Gender spinner setup
        String[] genderOptions = {"Male", "Female", ""};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setSelection(2);

        // Date picker for dob
        edtDob.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String dobStr = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                edtDob.setText(dobStr);
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        final com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        dialog.setContentView(dialogView);
        dialog.show();

        btnSave.setOnClickListener(v -> {
            // Validate
            String fullName = edtFullName.getText() != null ? edtFullName.getText().toString().trim() : "";
            String username = edtUsername.getText() != null ? edtUsername.getText().toString().trim() : "";
            String password = edtPassword.getText() != null ? edtPassword.getText().toString() : "";
            String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
            String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
            String address = edtAddress.getText() != null ? edtAddress.getText().toString().trim() : "";
            String dobStr = edtDob.getText() != null ? edtDob.getText().toString().trim() : "";
            java.time.LocalDateTime dob = null;
            try {
                dob = java.time.LocalDate.parse(dobStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            int genderVal = 2;
            String genderStr = spinnerGender.getSelectedItem().toString();
            if (genderStr.equalsIgnoreCase("Nam")) genderVal = 1;
            else if (genderStr.equalsIgnoreCase("Nữ")) genderVal = 0;

            String roleName = spinnerRole.getSelectedItem().toString();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            com.hashmal.tourapplication.service.dto.CreateSystemUserRequest req = new com.hashmal.tourapplication.service.dto.CreateSystemUserRequest();
            req.setFullName(fullName);
            req.setUsername(username);
            req.setPassword(password);
            req.setEmail(email);
            req.setPhoneNumber(phone);
            req.setAddress(address);
            req.setDob(dob.toString());
            req.setGender(genderVal);
            req.setRoleName(roleName);

            btnSave.setEnabled(false);
            apiService.createNewSysUser(req).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().getCode().equals(Code.SUCCESS.getCode())) {
                        Toast.makeText(requireContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadStaffs();
                    } else {
                        Toast.makeText(requireContext(), "Thêm nhân viên thất bại", Toast.LENGTH_SHORT).show();
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

    private void showDeleteStaffDialog(SysUserDTO user) {
        int status = user.getAccount().getStatus();
        String action = "";
        if (status == 1) {
            action = "vô hiệu hóa";
        } else if (status == 0) {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Thông báo")
                .setMessage("Tài khoản này chưa được kích hoạt hoặc đang chờ xác thực. Không thể thực hiện thao tác này.")
                .setPositiveButton("Tôi đã hiểu", (dialog, which) -> dialog.dismiss())
                .show();
            return;
        } else {
            action = "kích hoạt";
        }
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận")
            .setMessage("Bạn muốn " + action + " tài khoản này?\n\nTên: " + user.getProfile().getFullName() + "\nRole: " + user.getAccount().getRoleName())
            .setPositiveButton("Đúng", (dialog, which) -> {
                UpdateUserByAdminRequest req = new UpdateUserByAdminRequest();
                req.setAccountId(user.getAccount().getAccountId());
                req.setStatus(status == 1 ? -1 : 1);
                apiService.updateStaffByAdmin(req).enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null && Code.SUCCESS.getCode().equals(response.body().getCode())) {
                            Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            loadStaffs();
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
            .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
            .show();
    }
} 