package com.hashmal.tourapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.ScheduleDetailActivity;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TourScheduleListAdapter extends RecyclerView.Adapter<TourScheduleListAdapter.ScheduleViewHolder> {
    private List<TourScheduleResponseDTO> schedules;
    private final OnScheduleClickListener listener;

    public interface OnScheduleClickListener {
        void onScheduleClick(TourScheduleResponseDTO schedule);
    }

    public TourScheduleListAdapter(List<TourScheduleResponseDTO> schedules, OnScheduleClickListener listener) {
        this.schedules = schedules;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tour_schedule_card, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        TourScheduleResponseDTO schedule = schedules.get(position);
        holder.bind(schedule);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onScheduleClick(schedule);
            // Mở màn chi tiết
            Intent intent = new Intent(holder.itemView.getContext(), ScheduleDetailActivity.class);
            intent.putExtra("tourScheduleId", schedule.getTourScheduleId());
            intent.putExtra("tourId", schedule.getTourId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return schedules != null ? schedules.size() : 0;
    }

    public void updateData(List<TourScheduleResponseDTO> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvScheduleName, tvDateRange, tvTicketCount, tvStatus;
        private final LinearLayout scheduleLayout;
        private final Context context;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvScheduleName = itemView.findViewById(R.id.tvScheduleName);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvTicketCount = itemView.findViewById(R.id.tvTicketCount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            scheduleLayout = null; // Không dùng nữa
        }

        public void bind(TourScheduleResponseDTO schedule) {
            // Tên lịch trình
            String name = schedule.getTourScheduleId() != null ? schedule.getTourScheduleId() : "Lịch trình tour";
            tvScheduleName.setText(name);
            // Ngày bắt đầu - kết thúc
            try {
                String start = schedule.getStartTime();
                String end = schedule.getEndTime();
                LocalDateTime startDate, endDate;
                DateTimeFormatter inputFmt1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                DateTimeFormatter inputFmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                if (start.length() > 19) {
                    startDate = LocalDateTime.parse(start, inputFmt1);
                } else {
                    startDate = LocalDateTime.parse(start, inputFmt2);
                }
                if (end != null && end.length() > 19) {
                    endDate = LocalDateTime.parse(end, inputFmt1);
                } else if (end != null) {
                    endDate = LocalDateTime.parse(end, inputFmt2);
                } else {
                    endDate = startDate;
                }
                String text = outFmt.format(startDate);
                if (!startDate.equals(endDate)) {
                    text += " - " + outFmt.format(endDate);
                }
                tvDateRange.setText(text);
            } catch (Exception e) {
                tvDateRange.setText("");
            }
            // Số lượng vé còn lại
            tvTicketCount.setText("Còn " + (schedule.getNumberOfTicket() != null ? schedule.getNumberOfTicket() : "?") + " vé");
            // Trạng thái
//            int status = schedule.getStatus() != null ? schedule.getStatus() : -99;
//            String statusText;
//            int bgColor;
//            switch (status) {
//                case 1:
//                    statusText = "Đang hoạt động";
//                    bgColor = ContextCompat.getColor(context, R.color.status_confirmed);
//                    break;
//                case 0:
//                    statusText = "Chưa khởi hành";
//                    bgColor = ContextCompat.getColor(context, R.color.status_default);
//                    break;
//                case -1:
//                    statusText = "Đã hủy";
//                    bgColor = ContextCompat.getColor(context, R.color.status_cancelled);
//                    break;
//                default:
//                    statusText = "Không xác định";
//                    bgColor = ContextCompat.getColor(context, R.color.status_default);
//                    break;
//            }
            tvStatus.setText(DataUtils.getStringValueFromStatusValue(schedule.getStatus()));
//            tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, bgColor));
        }
    }
}