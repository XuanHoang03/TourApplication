package com.hashmal.tourapplication.adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import java.util.ArrayList;
import java.util.List;

public class AdminToursAdapter extends RecyclerView.Adapter<AdminToursAdapter.TourViewHolder> {
    private final Context context;
    private List<TourResponseDTO> tours = new ArrayList<>();
    private OnTourActionListener actionListener;

    public AdminToursAdapter(Context context) {
        this.context = context;
    }

    public void updateTours(List<TourResponseDTO> tours) {
        this.tours = tours;
        notifyDataSetChanged();
    }

    public void setOnTourActionListener(OnTourActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_tour, parent, false);
        return new TourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        TourResponseDTO tour = tours.get(position);
        holder.tvTourName.setText(tour.getTourName());
        holder.tvTourType.setText(tour.getTourType());
        String info = "Số người: " + tour.getNumberOfPeople() + " | Thời lượng: " + tour.getDuration();
        holder.tvTourInfo.setText(info);
        if (tour.getThumbnailUrl() != null && !tour.getThumbnailUrl().isEmpty()) {
            Glide.with(context)
                .load(tour.getThumbnailUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_tour)
                .into(holder.imgThumbnail);
        } else {
            holder.imgThumbnail.setImageResource(R.drawable.ic_tour);
        }

        if (tour.getPackages().isEmpty()) {
            holder.layout.setBackgroundResource(R.drawable.border_red);
            holder.tvAdditionalInfo.setText("Chương trình này chưa có dịch vụ!");
        }
        holder.btnArrow.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onViewTour(tour);
        });
        holder.layout.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onViewTour(tour);
        });
    }

    @Override
    public int getItemCount() {
        return tours != null ? tours.size() : 0;
    }

    public interface OnTourActionListener {
        void onViewTour(TourResponseDTO tour);
    }

    static class TourViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvTourName, tvTourType, tvTourInfo, tvAdditionalInfo;
        ImageButton btnArrow;
        LinearLayout layout;
        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvTourType = itemView.findViewById(R.id.tvTourType);
            tvTourInfo = itemView.findViewById(R.id.tvTourInfo);
            btnArrow = itemView.findViewById(R.id.btnArrow);
            layout = itemView.findViewById(R.id.itemLayout);
            tvAdditionalInfo = itemView.findViewById(R.id.tvAdditionalInfo);
        }
    }
} 