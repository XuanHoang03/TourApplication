package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.view.Gravity;
import android.graphics.Color;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.Code;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.LocationDTO;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.utils.DataUtils;
import java.util.List;

public class AdminTourDetailActivity extends AppCompatActivity {
    private TourResponseDTO tour;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tour_detail);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnEdit = findViewById(R.id.btnEdit);
        btnBack.setOnClickListener(v -> finish());
        String tourJson = getIntent().getStringExtra("tour");
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEditTourActivity.class);
            intent.putExtra("tour", tourJson);
            startActivity(intent);
        });
        tour = new Gson().fromJson(tourJson, TourResponseDTO.class);
        updateTourDetailUI(tour);
        Button btnAddPackage = findViewById(R.id.btnAddPackage);
        btnAddPackage.setOnClickListener(v -> {
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
                try { price = Long.parseLong(priceStr); } catch (Exception e) { edtPrice.setError("Giá không hợp lệ"); return; }
                com.hashmal.tourapplication.service.dto.CreatePackageRequest req = new com.hashmal.tourapplication.service.dto.CreatePackageRequest(tour.getTourId(), name, desc, price, isMain);
                btnSave.setEnabled(false);
                com.hashmal.tourapplication.network.ApiClient.getApiService().addPackage(req).enqueue(new retrofit2.Callback<com.hashmal.tourapplication.service.dto.BaseResponse>() {
                    @Override public void onResponse(retrofit2.Call<com.hashmal.tourapplication.service.dto.BaseResponse> call, retrofit2.Response<com.hashmal.tourapplication.service.dto.BaseResponse> response) {
                        btnSave.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && Code.SUCCESS.getCode().equals(response.body().getCode())) {
                            android.widget.Toast.makeText(AdminTourDetailActivity.this, "Thêm gói thành công", android.widget.Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            reloadTourDetail(tour.getTourId());
                        } else {
                            android.widget.Toast.makeText(AdminTourDetailActivity.this, "Thêm gói thất bại", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(retrofit2.Call<com.hashmal.tourapplication.service.dto.BaseResponse> call, Throwable t) {
                        btnSave.setEnabled(true);
                        android.widget.Toast.makeText(AdminTourDetailActivity.this, "Lỗi kết nối", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialog.show();
        });
    }

    private void reloadTourDetail(String tourId) {
        com.hashmal.tourapplication.network.ApiClient.getApiService().getAllTours().enqueue(new retrofit2.Callback<java.util.List<com.hashmal.tourapplication.service.dto.TourResponseDTO>>() {
            @Override public void onResponse(retrofit2.Call<java.util.List<com.hashmal.tourapplication.service.dto.TourResponseDTO>> call, retrofit2.Response<java.util.List<com.hashmal.tourapplication.service.dto.TourResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (com.hashmal.tourapplication.service.dto.TourResponseDTO t : response.body()) {
                        if (t.getTourId().equals(tourId)) {
                            tour = t;
                            updateTourDetailUI(t);
                            break;
                        }
                    }
                }
            }
            @Override public void onFailure(retrofit2.Call<java.util.List<com.hashmal.tourapplication.service.dto.TourResponseDTO>> call, Throwable t) { }
        });
    }

    private void updateTourDetailUI(com.hashmal.tourapplication.service.dto.TourResponseDTO tour) {
        ImageView img = findViewById(R.id.imgThumbnail);
        TextView tvName = findViewById(R.id.tvTourName);
        TextView tvType = findViewById(R.id.tvTourType);
        TextView tvDesc = findViewById(R.id.tvTourDescription);
        TextView tvNum = findViewById(R.id.tvNumberOfPeople);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvStart = findViewById(R.id.tvStartTime);
        TextView tvEnd = findViewById(R.id.tvEndTime);
        LinearLayout layoutLocations = findViewById(R.id.layoutSelectedLocations);
        RecyclerView rvPackages = findViewById(R.id.rvPackages);
        Glide.with(this).load(tour.getThumbnailUrl()).placeholder(R.drawable.ic_tour).into(img);
        tvName.setText(tour.getTourName());
        tvType.setText(tour.getTourType());
        tvDesc.setText(tour.getTourDescription());
        tvNum.setText("Số người: " + tour.getNumberOfPeople());
        tvDuration.setText("Thời lượng: " + tour.getDuration());
        tvStart.setText("Khởi hành: " + com.hashmal.tourapplication.utils.DataUtils.getStartOrEndTime(tour.getCurrentStartTime()));
        tvEnd.setText("Kết thúc: " + com.hashmal.tourapplication.utils.DataUtils.getStartOrEndTime(tour.getCurrentEndTime()));
        // Địa điểm
        List<com.hashmal.tourapplication.service.dto.LocationDTO> locations = tour.getLocations();
        layoutLocations.removeAllViews();
        if (locations != null && !locations.isEmpty()) {
            for (int i = 0; i < locations.size(); i++) {
                TextView tv = new TextView(this);
                tv.setText((i + 1) + ". " + locations.get(i).getName());
                layoutLocations.addView(tv);
            }
        }
        // Gói dịch vụ
        List<com.hashmal.tourapplication.service.dto.TourPackageDTO> packages = tour.getPackages();
        RecyclerView rv = findViewById(R.id.rvPackages);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PackageAdapter(packages));
    }

    private class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
        private final List<TourPackageDTO> packages;
        public PackageAdapter(List<TourPackageDTO> packages) {
            this.packages = packages;
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
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx);
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
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            layout.addView(tvName);
            TextView tvPrice = new TextView(parent.getContext());
            tvPrice.setTextSize(16);
            tvPrice.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvPrice.setSingleLine(true);
            tvPrice.setEllipsize(android.text.TextUtils.TruncateAt.END);
            tvPrice.setGravity(Gravity.END);
            layout.addView(tvPrice);
            card.addView(layout, new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.MATCH_PARENT));
            return new PackageViewHolder(card, tvName, tvPrice);
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
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}