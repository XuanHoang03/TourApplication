package com.hashmal.tourapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {
    private List<TourResponseDTO> tours;
    private Context context;
    private OnTourClickListener listener;

    public interface OnTourClickListener {
        void onTourClick(TourResponseDTO tour);
    }

    public TourAdapter(Context context, List<TourResponseDTO> tours, OnTourClickListener listener) {
        this.context = context;
        this.tours = tours;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        TourResponseDTO tour = tours.get(position);
        holder.bind(tour);
    }

    @Override
    public int getItemCount() {
        return tours != null ? tours.size() : 0;
    }

    public void updateTours(List<TourResponseDTO> newTours) {
        this.tours = newTours;
        notifyDataSetChanged();
    }

    class TourViewHolder extends RecyclerView.ViewHolder {
        private ImageView tourImage;
        private TextView tourName;
        private TextView tourLocation;
        private TextView tourPrice;

        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            tourImage = itemView.findViewById(R.id.tourImage);
            tourName = itemView.findViewById(R.id.tourName);
            tourLocation = itemView.findViewById(R.id.tourLocation);
            tourPrice = itemView.findViewById(R.id.tourPrice);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTourClick(tours.get(position));
                }
            });
        }

        public void bind(TourResponseDTO tour) {
            tourName.setText(tour.getTourName());
            
            // Set location (first location in the list)
            if (tour.getLocations() != null && !tour.getLocations().isEmpty()) {
                tourLocation.setText(tour.getLocations().get(0).getName());
            }

            // Set price (from main package)
            if (tour.getPackages() != null && !tour.getPackages().isEmpty()) {
                double price = tour.getPackages().get(0).getPrice();
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                tourPrice.setText(formatter.format(price));
            }

            // Load image using Glide
            if (tour.getThumbnailUrl() != null && !tour.getThumbnailUrl().isEmpty()) {
                Glide.with(context)
                     .load(tour.getThumbnailUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(tourImage);
            }
        }
    }
} 