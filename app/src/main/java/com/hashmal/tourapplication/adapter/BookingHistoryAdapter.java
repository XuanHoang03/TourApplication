package com.hashmal.tourapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.DisplayBookingDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {
    private List<DisplayBookingDTO> bookingList;
    private Context context;
    private SimpleDateFormat dateFormat;

    public BookingHistoryAdapter(List<DisplayBookingDTO> bookingList) {
        this.bookingList = bookingList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        DisplayBookingDTO booking = bookingList.get(position);

        // Set tour name
        holder.tvTourName.setText(booking.getBookingName());

        // Set dates
        holder.tvBookingDate.setText("Booked on: " + DataUtils.formatDateTimeString(booking.getBookingDate()));
        holder.tvTourDate.setText("Tour date: ");

        // Set number of people and price
        holder.tvNumberOfPeople.setText(booking.getQuantity().toString() + (booking.getQuantity() > 1 ? " tickets" : " ticket"));
        holder.tvTotalPrice.setText(DataUtils.formatCurrency(booking.getTotalPrice()));

        // Set status with color
//        holder.tvStatus.setText(booking.getPaymentStatus());
        setStatusColor(holder.tvStatus, booking.getPaymentStatus());

    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    private void setStatusColor(TextView statusView, Integer status) {
        if (status == null) status = -99; // hoặc giá trị default

        int colorResId;
        switch (status) {
            case 0:
                colorResId = R.color.status_pending;
                break;
            case -1:
                colorResId = R.color.status_cancelled;
                break;
            case 1:
                colorResId = R.color.status_completed;
                break;
            default:
                colorResId = R.color.status_default;
        }

        statusView.setTextColor(ContextCompat.getColor(context, colorResId));
    }

    public void updateData(List<DisplayBookingDTO> newBookingList) {
        this.bookingList = newBookingList;
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTourImage;
        TextView tvTourName;
        TextView tvBookingDate;
        TextView tvTourDate;
        TextView tvNumberOfPeople;
        TextView tvTotalPrice;
        TextView tvStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTourImage = itemView.findViewById(R.id.ivTourImage);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvTourDate = itemView.findViewById(R.id.tvTourDate);
            tvNumberOfPeople = itemView.findViewById(R.id.tvNumberOfPeople);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
} 