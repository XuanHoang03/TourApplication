package com.hashmal.tourapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.TourPackageDTO;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TourScheduleAdapter extends RecyclerView.Adapter<TourScheduleAdapter.ScheduleViewHolder> {
    private List<TourScheduleResponseDTO> schedules;
    private final Context context;
    private final DateTimeFormatter dateFormat;
    private final SimpleDateFormat scheduleFormat;
    private final OnScheduleClickListener listener;
    private int selectedPosition = -1;

    public interface OnScheduleClickListener {
        void onScheduleClick(TourScheduleResponseDTO schedule);
    }

    public TourScheduleAdapter(Context context, List<TourScheduleResponseDTO> schedules, OnScheduleClickListener listener) {
        this.context = context;
        this.schedules = schedules;
        this.listener = listener;
        this.scheduleFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        TourScheduleResponseDTO schedule = schedules.get(position);
        holder.bind(schedule);
        
        // Set selected state
        View itemView = holder.itemView.findViewById(R.id.scheduleLayout);
        if (position == selectedPosition) {
            itemView.setBackgroundResource(R.drawable.schedule_item_selected);
        } else {
            itemView.setBackgroundResource(R.drawable.schedule_item_background);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            
            // Update previous selected item
            if (previousSelected != -1) {
                notifyItemChanged(previousSelected);
            }
            
            // Update newly selected item
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onScheduleClick(schedule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules != null ? schedules.size() : 0;
    }

    public void updateSchedules(List<TourScheduleResponseDTO> newSchedules) {
        this.schedules = newSchedules;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView scheduleTime;
        private final LinearLayout scheduleLayout;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleTime = itemView.findViewById(R.id.scheduleTime);
            scheduleLayout = itemView.findViewById(R.id.scheduleLayout);
        }

        @SuppressLint("SetTextI18n")
        public void bind(TourScheduleResponseDTO schedule) {
            if (schedule != null) {
                LocalDateTime fromDateValue = LocalDateTime.parse(schedule.getStartTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDateTime toDateValue = LocalDateTime.parse(schedule.getEndTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                String fromDate = DataUtils.getVietNamFormatDateTime(fromDateValue);
                String toDate = DataUtils.getVietNamFormatDateTime(toDateValue);

                scheduleTime.setText(fromDate);
            }
        }
    }
} 