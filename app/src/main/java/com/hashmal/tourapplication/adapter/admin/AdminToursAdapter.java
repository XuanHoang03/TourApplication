package com.hashmal.tourapplication.adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEditTour(tour);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDeleteTour(tour);
        });
    }

    @Override
    public int getItemCount() {
        return tours != null ? tours.size() : 0;
    }

    public interface OnTourActionListener {
        void onEditTour(TourResponseDTO tour);
        void onDeleteTour(TourResponseDTO tour);
    }

    static class TourViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvTourName, tvTourType, tvTourInfo;
        ImageButton btnEdit, btnDelete;
        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvTourType = itemView.findViewById(R.id.tvTourType);
            tvTourInfo = itemView.findViewById(R.id.tvTourInfo);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 