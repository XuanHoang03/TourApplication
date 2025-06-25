package com.hashmal.tourapplication.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import android.view.Gravity;
import android.graphics.Color;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.TourPackageAdapter;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.enums.IntentResult;
import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.CreatePackageRequest;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTourDetailActivity extends AppCompatActivity {
    private TourResponseDTO tour;
    private PackageAdapter packageAdapter;
    private TourPackageDTO selectedPackage;
    TourResponseDTO currentTourInfo;
    private TourScheduleResponseDTO selectedSchedule;
    private List<TourPackageDTO> packages = new ArrayList<>();
    private List<TourScheduleResponseDTO> schedules = new ArrayList<>();
    private LocalDataService localDataService;
    private String tourJson;
    private ApiService apiService;
    private BaseResponse localResponse;

    private RecyclerView rv;
    private String tourId;
    Button btnDeletePackage, btnViewBooking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tour_detail);
        apiService = ApiClient.getApiService();
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnEdit = findViewById(R.id.btnEdit);
        btnBack.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated_tour", new Gson().toJson(tour));
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        tourId = getIntent().getStringExtra("tourId");
        loadTourInfo(tourId);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEditTourActivity.class);
            tourJson = new Gson().toJson(tour);
            intent.putExtra("tour", tourJson);
            startActivityForResult(intent, IntentResult.REQUEST_TOUR_EDIT.getValue());
        });
//        updateTourDetailUI(tour);

        Button btnAddPackage = findViewById(R.id.btnAddPackage);
        btnAddPackage.setOnClickListener(v -> showAddPackageDialog());

        btnDeletePackage = findViewById(R.id.btnDeletePackage);
        btnViewBooking = findViewById(R.id.btnViewBooking);
        btnViewBooking.setOnClickListener(v -> viewBookingClick());


        localDataService = LocalDataService.getInstance(this);
        if (localDataService.getSysUser().getAccount().getRoleName() != RoleEnum.SYSTEM_ADMIN.name()) {
            btnEdit.setVisibility(GONE);
            btnDeletePackage.setVisibility(GONE);
            btnAddPackage.setVisibility(GONE);
        }
        if (localDataService.getSysUser().getAccount().getRoleName().equals(RoleEnum.TOUR_OPERATOR.name())) {
            btnViewBooking.setVisibility(VISIBLE);
        }
    }

    private void viewBookingClick() {
        Intent bookingIntent = new Intent(AdminTourDetailActivity.this, CustomerTicketsActivity.class);
        bookingIntent.putExtra("tourId", tour.getTourId());
        startActivity(bookingIntent);
    }

    private void showConfirmDeleteDialog(Integer status) {
        String message = "";
        if (status == -1) {
            message = "Bạn có chắc chắn muốn dừng chương trình này không?";
        } else message = "Kích hoạt dịch vụ ?";


        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Có", (dialog, which) -> {
                    asyncModifyTourStatus(status).thenAccept(success -> {
                        if (localResponse.getCode().equals(Code.SUCCESS.getCode())) {
                            new MaterialAlertDialogBuilder(this)
                                    .setTitle("Thành công")
                                    .setMessage(localResponse.getMessage())
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setPositiveButton("OK", (dialog2, which2) -> {
                                        dialog2.dismiss();
                                        setResult(RESULT_OK);
                                        finish();
                                    })
                                    .show();
                        }
                    });
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private CompletableFuture<Boolean> asyncModifyTourStatus(Integer status) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        apiService.modifyTourStatus(tour.getTourId(), status).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    localResponse = response.body();
                    future.complete(true);
                } else {
                    future.complete(false);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                future.complete(false);
            }
        });
        return future;
    }


    private void showAddPackageDialog() {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).create();
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_add_package, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        android.widget.ImageButton btnBackDialog = dialogView.findViewById(R.id.btnBackDialog);
        btnBackDialog.setOnClickListener(v1 -> dialog.dismiss());

        android.widget.EditText edtName = dialogView.findViewById(R.id.edtPackageName);
        android.widget.EditText edtDesc = dialogView.findViewById(R.id.edtPackageDescription);
        android.widget.EditText edtPrice = dialogView.findViewById(R.id.edtPackagePrice);
        android.widget.CheckBox cbIsMain = dialogView.findViewById(R.id.cbIsMain);
        Button btnSave = dialogView.findViewById(R.id.btnSavePackage);

        btnSave.setOnClickListener(v2 -> {
            String name = edtName.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            boolean isMain = cbIsMain.isChecked();

            if (name.isEmpty() || priceStr.isEmpty()) {
                edtName.setError(name.isEmpty() ? "Bắt buộc" : null);
                edtPrice.setError(priceStr.isEmpty() ? "Bắt buộc" : null);
                return;
            }

            Long price;
            try {
                price = Long.parseLong(priceStr);
            } catch (Exception e) {
                edtPrice.setError("Giá không hợp lệ");
                return;
            }

            CreatePackageRequest req =
                    new CreatePackageRequest(tour.getTourId(), name, desc, price, isMain);

            btnSave.setEnabled(false);

            apiService.addPackage(req)
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<BaseResponse> call,
                                               retrofit2.Response<BaseResponse> response) {
                            btnSave.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null &&
                                    Code.SUCCESS.getCode().equals(response.body().getCode())) {

                                android.widget.Toast.makeText(AdminTourDetailActivity.this, "Thêm gói thành công",
                                        android.widget.Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                                // Tạo package mới và thêm vào list
                                TourPackageDTO newPackage = new TourPackageDTO();
                                newPackage.setPackageName(name);
                                newPackage.setDescription(desc);
                                newPackage.setPrice(price);
                                newPackage.setMain(isMain);

                                if (tour.getPackages() == null) {
                                    tour.setPackages(new ArrayList<>());
                                }
                                tour.getPackages().add(newPackage);
                            } else {
                                android.widget.Toast.makeText(AdminTourDetailActivity.this, "Thêm gói thất bại",
                                        android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<BaseResponse> call, Throwable t) {
                            btnSave.setEnabled(true);
                            android.widget.Toast.makeText(AdminTourDetailActivity.this, "Lỗi kết nối",
                                    android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }


    public void loadTourInfo(String tourId) {
        apiService.getTourInfo(tourId).enqueue(new Callback<TourResponseDTO>() {
            @Override
            public void onResponse(Call<TourResponseDTO> call, Response<TourResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTourInfo = response.body();
                    tour = currentTourInfo;
                    updateTourDetailUI(currentTourInfo);
                    packages = currentTourInfo.getPackages();
                    setupPackagesRecyclerView(packages);
                    if (tour.getStatus().equals(-1)) {
                        btnDeletePackage.setText("* Kích hoạt chương trình");
                        btnDeletePackage.setBackgroundTintList(ContextCompat.getColorStateList(AdminTourDetailActivity.this, R.color.blue));
                        btnDeletePackage.setOnClickListener(v -> showConfirmDeleteDialog(1));
                    } else btnDeletePackage.setOnClickListener(v -> showConfirmDeleteDialog(-1));
                } else {
                    currentTourInfo = null;
                }
            }

            @Override
            public void onFailure(Call<TourResponseDTO> call, Throwable t) {
                Toast.makeText(AdminTourDetailActivity.this, "Error when calling api get tour info", Toast.LENGTH_SHORT).show();
                currentTourInfo = null;
            }
        });
    }

    private void setupPackagesRecyclerView(List<TourPackageDTO> tourPackages) {
        try {
            if (tourPackages == null) {
                tourPackages = new ArrayList<>();
                Log.d("TourDetail", "Tour packages list is null, initializing empty list");
            }

            packageAdapter = new PackageAdapter(tourPackages);

            if (rv != null) {
                rv.setLayoutManager(new LinearLayoutManager(this));
                rv.setAdapter(packageAdapter);
                Log.d("TourDetail", "Successfully set up RecyclerView with " + tourPackages.size() + " packages");
            } else {
                Log.e("TourDetail", "RecyclerView is null");
            }
        } catch (Exception e) {
            Log.e("TourDetail", "Error setting up packages RecyclerView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTourDetailUI(TourResponseDTO tour) {
        ImageView img = findViewById(R.id.imgThumbnail);
        TextView tvName = findViewById(R.id.tvTourName);
        TextView tvType = findViewById(R.id.tvTourType);
        TextView tvDesc = findViewById(R.id.tvTourDescription);
        TextView tvNum = findViewById(R.id.tvNumberOfPeople);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvStart = findViewById(R.id.tvStartTime);
        TextView tvEnd = findViewById(R.id.tvEndTime);
        LinearLayout layoutLocations = findViewById(R.id.layoutSelectedLocations);

        Glide.with(this).load(tour.getThumbnailUrl()).placeholder(R.drawable.ic_tour).into(img);
        tvName.setText(tour.getTourName());
        tvType.setText(tour.getTourType());
        tvDesc.setText(tour.getTourDescription());
        tvNum.setText("Số người: " + tour.getNumberOfPeople());
        tvDuration.setText("Thời lượng: " + tour.getDuration());
        tvStart.setText("Khởi hành: " + com.hashmal.tourapplication.utils.DataUtils.getStartOrEndTime(tour.getCurrentStartTime()));
        tvEnd.setText("Kết thúc: " + com.hashmal.tourapplication.utils.DataUtils.getStartOrEndTime(tour.getCurrentEndTime()));

        // Hiển thị địa điểm
        List<com.hashmal.tourapplication.service.dto.LocationDTO> locations = tour.getLocations();
        layoutLocations.removeAllViews();
        if (locations != null && !locations.isEmpty()) {
            for (int i = 0; i < locations.size(); i++) {
                TextView tv = new TextView(this);
                tv.setText((i + 1) + ". " + locations.get(i).getName());
                layoutLocations.addView(tv);
            }
        }

        // Hiển thị gói dịch vụ
        List<com.hashmal.tourapplication.service.dto.TourPackageDTO> packages = tour.getPackages();
        rv = findViewById(R.id.rvPackages);
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (packageAdapter == null) {
            // Tạo adapter lần đầu
            packageAdapter = new PackageAdapter(packages);
            rv.setAdapter(packageAdapter);
        } else {
            // Cập nhật adapter đã có
            packageAdapter.updatePackages(packages);
        }
    }

    private class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
        private List<TourPackageDTO> packages;

        public PackageAdapter(List<TourPackageDTO> packages) {
            this.packages = packages != null ? packages : new ArrayList<>();
        }

        // Method để cập nhật toàn bộ danh sách
        public void updatePackages(List<TourPackageDTO> newPackages) {
            this.packages.clear();
            if (newPackages != null) {
                this.packages.addAll(newPackages);
            }
            notifyDataSetChanged();
        }

        // Method để cập nhật 1 package cụ thể
        public void updatePackage(int position, TourPackageDTO updatedPackage) {
            if (position >= 0 && position < packages.size()) {
                packages.set(position, updatedPackage);
                notifyItemChanged(position);
            }
        }

        // Thêm package mới
        public void addPackage(TourPackageDTO newPackage) {
            packages.add(newPackage);
            notifyItemInserted(packages.size() - 1);
        }

        // Xóa package
        public void removePackage(int position) {
            if (position >= 0 && position < packages.size()) {
                packages.remove(position);
                notifyItemRemoved(position);
            }
        }

        @Override
        public int getItemCount() {
            return packages == null ? 0 : packages.size();
        }

        @Override
        public PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int heightPx = (int) (80 * parent.getContext().getResources().getDisplayMetrics().density);
            int marginPx = (int) (8 * parent.getContext().getResources().getDisplayMetrics().density);

            CardView card = new CardView(parent.getContext());
            card.setCardElevation(6f);
            card.setRadius(24f);
            card.setCardBackgroundColor(Color.WHITE);

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, heightPx);
            params.setMargins(marginPx, marginPx, marginPx, marginPx);
            card.setLayoutParams(params);

            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.setPadding(32, 0, 32, 0);

            TextView tvName = new TextView(parent.getContext());
            tvName.setTextSize(16);
            tvName.setTextColor(Color.BLACK);
            tvName.setSingleLine(true);
            tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            layout.addView(tvName);

            TextView tvPrice = new TextView(parent.getContext());
            tvPrice.setTextSize(16);
            tvPrice.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvPrice.setSingleLine(true);
            tvPrice.setEllipsize(android.text.TextUtils.TruncateAt.END);
            tvPrice.setGravity(Gravity.END);
            layout.addView(tvPrice);

            card.addView(layout, new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.MATCH_PARENT));

            PackageViewHolder holder = new PackageViewHolder(card, tvName, tvPrice);

            card.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TourPackageDTO pkg = packages.get(position);
                    Intent intent = new Intent(AdminTourDetailActivity.this, AdminPackageDetailActivity.class);
                    intent.putExtra("package", new Gson().toJson(pkg));
                    intent.putExtra("package_position", position); // Truyền thêm position
                    startActivityForResult(intent, IntentResult.REQUEST_PACKAGE_EDIT.getValue());
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(PackageViewHolder holder, int position) {
            TourPackageDTO pkg = packages.get(position);
            holder.tvName.setText(pkg.getPackageName());
            holder.tvPrice.setText(DataUtils.formatCurrency(pkg.getPrice()));
        }

        class PackageViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice;

            public PackageViewHolder(CardView itemView, TextView tvName, TextView tvPrice) {
                super(itemView);
                this.tvName = tvName;
                this.tvPrice = tvPrice;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentResult.REQUEST_PACKAGE_EDIT.getValue()) {
            if (resultCode == RESULT_OK && data != null) {

                String pkgStr = data.getStringExtra("package");
                int position = data.getIntExtra("package_position", -1);

                if (pkgStr != null) {
                    TourPackageDTO updatedPackage = new Gson().fromJson(pkgStr, TourPackageDTO.class);

                    if (position != -1) {
                        // Cập nhật theo position
                        if (position < tour.getPackages().size()) {
                            tour.getPackages().set(position, updatedPackage);
                            packageAdapter.updatePackage(position, updatedPackage);
                        }
                    } else {
                        // Tìm theo ID nếu không có position
                        List<TourPackageDTO> currentPackages = tour.getPackages();
                        for (int i = 0; i < currentPackages.size(); i++) {
                            if (currentPackages.get(i).getId().equals(updatedPackage.getId())) {
                                currentPackages.set(i, updatedPackage);
                                packageAdapter.updatePackage(i, updatedPackage);
                                break;
                            }
                        }
                    }
                }
            } else if (resultCode == IntentResult.DELETE_PACKAGE.getValue() && data != null) {
                int position = data.getIntExtra("position", -1);
                packageAdapter.removePackage(position);
            }
        }
        if (requestCode == IntentResult.REQUEST_PACKAGE_EDIT.getValue() && resultCode == RESULT_OK && data != null) {
            loadTourInfo(tourId);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_tour", new Gson().toJson(tour));
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}