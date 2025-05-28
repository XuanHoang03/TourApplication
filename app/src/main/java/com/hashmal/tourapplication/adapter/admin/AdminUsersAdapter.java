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
import com.hashmal.tourapplication.service.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.UserViewHolder> {

    private Context context;
    private List<UserDTO> users;
    private OnUserActionListener listener;
    private OnUserClickListener userClickListener;

    public interface OnUserActionListener {
        void onEditUser(UserDTO user);
        void onDeleteUser(UserDTO user);
    }

    public interface OnUserClickListener {
        void onUserClick(UserDTO user);
    }

    public AdminUsersAdapter(Context context) {
        this.context = context;
        this.users = new ArrayList<>();
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.userClickListener = listener;
    }

    public void updateUsers(List<UserDTO> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDTO user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvStatus;
        private ImageButton btnEdit, btnDelete;
        private ImageView imgAvatar;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditUser(users.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteUser(users.get(position));
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && userClickListener != null) {
                    userClickListener.onUserClick(users.get(position));
                }
            });
        }

        void bind(UserDTO user) {
            tvName.setText(user.getProfile().getFullName());
            Integer status = user.getAccount().getStatus();
            switch  (status) {
                case 1:
                    tvStatus.setText("Active");
                    tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case -1:
                    tvStatus.setText("Inactive");
                    tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.holo_red_dark));
                    break;
                case 0:
                    tvStatus.setText("Registered");
                    tvStatus.setTextColor(itemView.getResources().getColor(android.R.color.darker_gray));
                    break;
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