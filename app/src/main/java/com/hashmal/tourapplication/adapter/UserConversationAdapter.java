package com.hashmal.tourapplication.adapter;

import android.annotation.SuppressLint;
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
import com.hashmal.tourapplication.service.FirebaseService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.UserChatInfo;
import com.hashmal.tourapplication.service.dto.UserDTO;
import com.hashmal.tourapplication.utils.DataUtils;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConversationAdapter extends RecyclerView.Adapter<UserConversationAdapter.ConversationViewHolder> {
    private List<UserConversation> conversations;
    private Map<String, UserChatInfo> chatInfo;
    private Context context;

    public UserConversationAdapter(Context context, List<UserConversation> conversations, Map<String, UserChatInfo> chatInfo) {
        this.context = context;
        this.conversations = conversations;
        this.chatInfo = chatInfo;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
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

            UserChatInfo info = chatInfo.get(otherUserId);
                    if (info != null) {
                    holder.usernameTextView.setText(info.getFullName());
                        Glide.with(context)
                                .load(info.getAvatarUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(holder.avatarImageView);

                    if (!conversation.getLastSendBy().equals(currentUserId)) {
                        holder.lastMessage.setText(info.getFullName() + ": " + conversation.getLastMessageContent());
                    } else {
                        holder.lastMessage.setText(conversation.getLastMessageContent());
                    }
                } else {
                     info = new UserChatInfo("SYSTEM","Tin nhắn hệ thống", "" );
                     chatInfo.put(info.getAccountId(), info);

                    holder.usernameTextView.setText("Tin nhắn hệ thống");
                        Glide.with(context)
                                .load("")
                                .circleCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(holder.avatarImageView);
                    holder.lastMessage.setText("Tin nhắn hệ thống: " + conversation.getLastMessageContent());
                }

                holder.timestamp.setText(DataUtils.getVietNamFormatDateTime(conversation.getLastSend()));
            UserChatInfo finalInfo = info;
            holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("conversationId", conversation.getId());
                    intent.putExtra("otherUserJson", new Gson().toJson(finalInfo));
                    context.startActivity(intent);
                });
        });
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

    public void updateList(List<UserConversation> newList, Map<String, UserChatInfo> map) {
        this.conversations.clear();
        this.conversations.addAll(newList);
        this.chatInfo.clear();
        this.chatInfo = map;
        notifyDataSetChanged();
    }

}
