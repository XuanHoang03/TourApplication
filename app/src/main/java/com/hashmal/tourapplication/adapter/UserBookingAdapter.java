package com.hashmal.tourapplication.adapter;

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
import com.hashmal.tourapplication.service.dto.UserBookingDTO;
import androidx.cardview.widget.CardView;
import java.util.List;

public class UserBookingAdapter extends RecyclerView.Adapter<UserBookingAdapter.BuyerViewHolder> {
    private List<UserBookingDTO> buyers;
    private OnBuyerActionListener listener;
    private OnItemClickListener itemClickListener;
    private Context context;

    public interface OnBuyerActionListener {
        void onModifyBooking(UserBookingDTO booking);
        void onCancelBooking(UserBookingDTO booking);
    }

    public interface OnItemClickListener {
        void onItemClick(UserBookingDTO booking);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public UserBookingAdapter(List<UserBookingDTO> buyers, OnBuyerActionListener listener) {
        this.buyers = buyers;
        this.listener = listener;
    }

    public void updateData(List<UserBookingDTO> buyers) {
        this.buyers = buyers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BuyerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_buyer, parent, false);
        return new BuyerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyerViewHolder holder, int position) {
        UserBookingDTO buyer = buyers.get(position);
        holder.tvName.setText(buyer.getUser().getProfile().getFullName());
        holder.tvPhone.setText(buyer.getUser().getProfile().getPhoneNumber());
        holder.tvQuantity.setText("Số vé: " + (buyer.getBooking() != null && buyer.getBooking().getQuantity() != null ? buyer.getBooking().getQuantity() : "?"));

        // Load avatar
        String avatarUrl = buyer.getUser().getProfile() != null ? buyer.getUser().getProfile().getAvatarUrl() : null;
        Glide.with(context)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_profile)
            .circleCrop()
            .into(holder.imgUserIcon);

        // Set border đỏ nếu status = -11
        Integer status = buyer.getBooking() != null ? buyer.getBooking().getPaymentStatus() : null;
        if (status != null && status == -11) {
            holder.cardView.setBackgroundResource(R.drawable.bg_buyer_border_red);
            holder.btnModify.setEnabled(false);
            holder.btnCancel.setEnabled(false);
            holder.tvQuantity.setText("Đã hủy vé này.");
        } else {
            holder.cardView.setBackgroundResource(0); // Xóa custom background, dùng mặc định
        }

        if (listener != null) {
            holder.btnModify.setOnClickListener(v -> listener.onModifyBooking(buyer));
            holder.btnCancel.setOnClickListener(v -> listener.onCancelBooking(buyer));
        }
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(buyer));
        }
    }

    @Override
    public int getItemCount() {
        return buyers != null ? buyers.size() : 0;
    }

    static class BuyerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvQuantity;
        ImageButton btnModify, btnCancel;
        ImageView imgUserIcon;
        CardView cardView;

        public BuyerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBuyerName);
            tvPhone = itemView.findViewById(R.id.tvBuyerPhone);
            tvQuantity = itemView.findViewById(R.id.tvBuyerQuantity);
            btnModify = itemView.findViewById(R.id.btnModify);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            imgUserIcon = itemView.findViewById(R.id.imgUserIcon);
            cardView = (CardView) itemView;
        }
    }
} 