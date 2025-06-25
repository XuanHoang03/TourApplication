package com.hashmal.tourapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.enums.StatusEnum;
import com.hashmal.tourapplication.service.dto.TourGuideScheduleDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TourGuideScheduleAdapter extends RecyclerView.Adapter<TourGuideScheduleAdapter.ScheduleViewHolder> {
    private final List<TourGuideScheduleDTO> schedules;

    public TourGuideScheduleAdapter(List<TourGuideScheduleDTO> schedules) {
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour_guide_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        holder.bind(schedules.get(position));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTourName, tvStartTime, tvEndTime, tvStatus, tvTicketCount;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTicketCount = itemView.findViewById(R.id.tvTicketCount);
        }

        public void bind(TourGuideScheduleDTO schedule) {
            tvTourName.setText(schedule.getTourName() != null ? schedule.getTourName() : "");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tvStartTime.setText("Bắt đầu: " + (schedule.getStartTime() != null ? DataUtils.formatDateTimeString(schedule.getStartTime(), true) : ""));
            tvEndTime.setText("Kết thúc: " + (schedule.getEndTime() != null ? DataUtils.formatDateTimeString(schedule.getEndTime(), true) : ""));
            tvStatus.setText("Trạng thái: " + (schedule.getStatus() != null ? DataUtils.getStringValueFromStatusValue( schedule.getStatus() ): ""));
            tvTicketCount.setText("Số vé: " + (schedule.getNumber_of_ticket() != null ? schedule.getNumber_of_ticket() : ""));

            if (schedule.getStatus().equals(0)) {
                tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_orange_dark));
            } else if (schedule.getStatus().equals(1)) {
                tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_green_dark));
            } else if (schedule.getStatus().equals(-1)) {
                tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_red_dark));
            } else if (schedule.getStatus().equals(11)) {
                tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_blue_dark));
            }


        }
    }
} 