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
        TourResponseDTO tour = new Gson().fromJson(tourJson, TourResponseDTO.class);
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
        Button btnAddPackage = findViewById(R.id.btnAddPackage);
        Glide.with(this).load(tour.getThumbnailUrl()).placeholder(R.drawable.ic_tour).into(img);
        tvName.setText(tour.getTourName());
        tvType.setText(tour.getTourType());
        tvDesc.setText(tour.getTourDescription());
        tvNum.setText("Số người: " + tour.getNumberOfPeople());
        tvDuration.setText("Thời lượng: " + tour.getDuration());
        tvStart.setText("Khởi hành: " + DataUtils.getStartOrEndTime(tour.getCurrentStartTime()));
        tvEnd.setText("Kết thúc: " + DataUtils.getStartOrEndTime(tour.getCurrentEndTime()));
        // Địa điểm
        List<LocationDTO> locations = tour.getLocations();
        layoutLocations.removeAllViews();
        if (locations != null && !locations.isEmpty()) {
            for (int i = 0; i < locations.size(); i++) {
                TextView tv = new TextView(this);
                tv.setText((i + 1) + ". " + locations.get(i).getName());
                layoutLocations.addView(tv);
            }
        }
        // Gói dịch vụ
        List<TourPackageDTO> packages = tour.getPackages();
        rvPackages.setLayoutManager(new LinearLayoutManager(this));
        rvPackages.setAdapter(new PackageAdapter(packages));
        btnAddPackage.setOnClickListener(v -> {
            // TODO: Hiển thị dialog thêm gói dịch vụ
        });
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
}