package com.hashmal.tourapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class TourPackageAdapter extends RecyclerView.Adapter<TourPackageAdapter.PackageViewHolder> {
    private List<TourPackageDTO> packages;
    private final Context context;
    private final OnPackageClickListener listener;
    private int isPackageSelected = -1;

    public interface OnPackageClickListener {
        void onPackageClick(TourPackageDTO tourPackage);
    }

    public TourPackageAdapter(Context context, List<TourPackageDTO> packages, OnPackageClickListener listener) {
        this.context = context;
        this.packages = packages != null ? packages : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_tour_package, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        if (position < packages.size()) {
            TourPackageDTO tourPackage = packages.get(position);
            holder.bind(tourPackage);
            View itemView = holder.itemView.findViewById(R.id.linearLayout);
            Button selectBtn = holder.itemView.findViewById(R.id.selectPackageButton);
            if (position == isPackageSelected) {
                itemView.setBackgroundResource(R.drawable.schedule_item_selected);
            } else {
                itemView.setBackgroundResource(R.drawable.schedule_item_background);
            }
            selectBtn.setOnClickListener(v -> {


                int previousSelected = isPackageSelected;
                isPackageSelected = holder.getAdapterPosition();

                // Update previous selected item
                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected);
                }

                // Update newly selected item
                notifyItemChanged(isPackageSelected);

                if (listener != null) {
                    listener.onPackageClick(tourPackage);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public void updatePackages(List<TourPackageDTO> newPackages) {
        this.packages = newPackages != null ? newPackages : new ArrayList<>();
        notifyDataSetChanged();
    }

    class PackageViewHolder extends RecyclerView.ViewHolder {
        private final TextView packageName;
        private final TextView packageDescription;
        private final TextView packagePrice;
        private final Button selectPackageButton;

        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.packageName);
            packageDescription = itemView.findViewById(R.id.packageDescription);
            packagePrice = itemView.findViewById(R.id.packagePrice);
            selectPackageButton = itemView.findViewById(R.id.selectPackageButton);
        }

        public void bind(TourPackageDTO tourPackage) {
            if (tourPackage != null) {
                packageName.setText(tourPackage.getPackageName());
                packageDescription.setText(tourPackage.getDescription());
                packagePrice.setText(DataUtils.formatCurrency(tourPackage.getPrice()));

                selectPackageButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPackageClick(tourPackage);
                    }
                });
            }
        }
    }
} 