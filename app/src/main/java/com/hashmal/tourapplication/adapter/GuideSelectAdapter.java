package com.hashmal.tourapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import java.util.List;

public class GuideSelectAdapter extends RecyclerView.Adapter<GuideSelectAdapter.GuideViewHolder> {
    private List<SysUserDTO> guides;
    private final OnGuideClickListener listener;

    public interface OnGuideClickListener {
        void onGuideClick(SysUserDTO guide);
    }

    public GuideSelectAdapter(List<SysUserDTO> guides, OnGuideClickListener listener) {
        this.guides = guides;
        this.listener = listener;
    }

    public void updateData(List<SysUserDTO> newGuides) {
        this.guides = newGuides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guide_select, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        SysUserDTO guide = guides.get(position);
        holder.bind(guide);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGuideClick(guide);
        });
    }

    @Override
    public int getItemCount() {
        return guides != null ? guides.size() : 0;
    }

    static class GuideViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgAvatar;
        private final TextView tvName, tvPhone, tvEmail;
        public GuideViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
        public void bind(SysUserDTO guide) {
            String avatarUrl = guide.getProfile() != null ? guide.getProfile().getAvatarUrl() : null;
            Glide.with(itemView.getContext())
                .load(avatarUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(imgAvatar);
            tvName.setText(guide.getProfile() != null && guide.getProfile().getFullName() != null ? guide.getProfile().getFullName() : "Không tên");
            tvPhone.setText(guide.getProfile() != null && guide.getProfile().getPhoneNumber() != null ? guide.getProfile().getPhoneNumber() : "");
            tvEmail.setText(guide.getProfile() != null && guide.getProfile().getEmail() != null ? guide.getProfile().getEmail() : "");
        }
    }
} 