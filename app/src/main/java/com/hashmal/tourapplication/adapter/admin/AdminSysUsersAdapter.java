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
import com.hashmal.tourapplication.service.dto.SysUserDTO;

import java.util.ArrayList;
import java.util.List;

public class AdminSysUsersAdapter extends RecyclerView.Adapter<AdminSysUsersAdapter.SysUserViewHolder> {
    private Context context;
    private List<SysUserDTO> sysUsers;
    private OnSysUserActionListener listener;
    private OnSysUserClickListener userClickListener;
    private boolean showEditDeleteButtons = true;

    public interface OnSysUserActionListener {
        void onEditSysUser(SysUserDTO user);
        void onDeleteSysUser(SysUserDTO user);
    }

    public interface OnSysUserClickListener {
        void onSysUserClick(SysUserDTO user);
    }

    public AdminSysUsersAdapter(Context context) {
        this.context = context;
        this.sysUsers = new ArrayList<>();
    }

    public void setOnSysUserActionListener(OnSysUserActionListener listener) {
        this.listener = listener;
    }

    public void setOnSysUserClickListener(OnSysUserClickListener listener) {
        this.userClickListener = listener;
    }

    public void updateSysUsers(List<SysUserDTO> newUsers) {
        this.sysUsers.clear();
        this.sysUsers.addAll(newUsers);
        notifyDataSetChanged();
    }

    public void setShowEditDeleteButtons(boolean show) {
        this.showEditDeleteButtons = show;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SysUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new SysUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SysUserViewHolder holder, int position) {
        SysUserDTO user = sysUsers.get(position);
        holder.bind(user);
    }


    @Override
    public int getItemCount() {
        return sysUsers.size();
    }

    class SysUserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvRole, tvStatus;
        private ImageButton btnEdit, btnDelete;
        private ImageView imgAvatar;


        SysUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditSysUser(sysUsers.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteSysUser(sysUsers.get(position));
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && userClickListener != null) {
                    userClickListener.onSysUserClick(sysUsers.get(position));
                }
            });

            btnEdit.setVisibility(showEditDeleteButtons ? View.VISIBLE : View.GONE);
            btnDelete.setVisibility(showEditDeleteButtons ? View.VISIBLE : View.GONE);
        }



        void bind(SysUserDTO user) {
            tvName.setText(user.getProfile().getFullName());
            String role = user.getAccount().getRoleName();
            if (role != null) {
                switch (role) {
                    case "Staff":
                    case "TOUR_GUIDE":
                        tvRole.setTextColor(itemView.getResources().getColor(R.color.teal_700));
                        tvRole.setText("Hướng dẫn viên");
                        break;
                    case "Admin":
                    case "SYSTEM_ADMIN":
                        tvRole.setText("Quản trị viên");
                        tvRole.setTextColor(itemView.getResources().getColor(android.R.color.holo_red_dark));
                        break;
                    case "Manager":
                    case "TOUR_OPERATOR":
                        tvRole.setText("Điều hành viên");
                        tvRole.setTextColor(itemView.getResources().getColor(android.R.color.holo_blue_dark));
                        break;
                    default:
                        tvRole.setTextColor(itemView.getResources().getColor(android.R.color.darker_gray));
                        break;
                }
            } else {
                tvRole.setText("");
            }
            Integer status = user.getAccount().getStatus();
            if (status != null) {
                switch (status) {
                    case 1:
                        tvStatus.setText("Đang hoạt động");
                        tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_green_dark));
                        break;
                    case -1:
                        tvStatus.setText("Đã vô hiệu hóa");
                        tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_red_dark));
                        break;
                    case 0:
                        tvStatus.setText("Chưa kích hoạt");
                        tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.darker_gray));
                        break;
                    default:
                        tvStatus.setText("");
                        break;
                }
            } else {
                tvStatus.setText("");
            }
            String avatarUrl = user.getProfile().getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_profile);
            }
        }
    }
} 