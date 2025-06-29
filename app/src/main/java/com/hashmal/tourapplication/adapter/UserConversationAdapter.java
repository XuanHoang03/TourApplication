package com.hashmal.tourapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.ChatActivity;
import com.hashmal.tourapplication.entity.UserConversation;
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.FirebaseService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConversationAdapter extends RecyclerView.Adapter<UserConversationAdapter.ConversationViewHolder> {
    private List<UserConversation> conversations;
    private Context context;

    public UserConversationAdapter(Context context, List<UserConversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        FirebaseService firebaseService = new FirebaseService(FirebaseFirestore.getInstance());
        LocalDataService localDataService = LocalDataService.getInstance(context);
        UserConversation conversation = conversations.get(position);

        String currentUserId = localDataService.getCurrentUser().getAccount().getAccountId();

        firebaseService.getListUserInChat(conversation.getId(), userIds -> {
            // 1️⃣ Lấy được user khác
            String otherUserId = null;
            for (String id : userIds) {
                if (!id.equals(currentUserId)) {
                    otherUserId = id;
                    break;
                }
            }

            if (otherUserId == null) {
                holder.lastMessage.setText("Không tìm thấy người dùng");
                return;
            }

            getUserInfoById(otherUserId, user -> {
                if (user != null) {
                    holder.usernameTextView.setText(user.getProfile().getFullName());
                    if (user.getProfile().getAvatarUrl() != null) {
                        Glide.with(context)
                                .load(user.getProfile().getAvatarUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(holder.avatarImageView);
                    }
                    if (!conversation.getLastSendBy().equals(currentUserId)) {
                        holder.lastMessage.setText(user.getProfile().getFullName() + ": " + conversation.getLastMessageContent());
                    } else {
                        holder.lastMessage.setText(conversation.getLastMessageContent());
                    }
                } else {
                    holder.lastMessage.setText("Không xác định: " + conversation.getLastMessageContent());
                }

                holder.timestamp.setText(DataUtils.getVietNamFormatDateTime(conversation.getLastSend()));
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("conversationId", conversation.getId());
                    intent.putExtra("otherUserJson", new Gson().toJson( user));
                    context.startActivity(intent);
                });
            });
        });
    }

    public void getUserInfoById(String userId, OnUserFetchedCallback callback) {
        Call<UserDTO> call = ApiClient.getApiService().getFullUserInformationById(userId);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onUserFetched(response.body());
                } else {
                    callback.onUserFetched(null); // hoặc callback.onError() nếu có
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                callback.onUserFetched(null); // hoặc xử lý lỗi riêng nếu muốn
            }
        });
    }

    public interface OnUserFetchedCallback {
        void onUserFetched(UserDTO user);
    }


    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView lastMessage, timestamp, usernameTextView;
        ImageView avatarImageView;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessage = itemView.findViewById(R.id.lastMessageTextView);
            timestamp = itemView.findViewById(R.id.timestampTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }
    }

    public void updateList(List<UserConversation> newList) {
        this.conversations.clear();
        this.conversations.addAll(newList);
        notifyDataSetChanged();
    }

}
