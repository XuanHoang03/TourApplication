package com.hashmal.tourapplication.adapter;

import static androidx.appcompat.content.res.AppCompatResources.getColorStateList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.entity.Message;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messageList;
    private String currentUserId;
    private Context context;
    public MessageAdapter(List<Message> messageList,String currentUserId, Context context) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_right, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
        }
        return new MessageViewHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        Message msg = messageList.get(position);
        return msg.getCreatedBy().equals("Bạn") ? 1 : 0;
    }
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.contentTextView.setText(message.getContent());
        holder.senderTextView.setText(message.getCreatedBy());

        if (message.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = sdf.format(message.getCreatedAt());
            holder.timeTextView.setText(time);
        } else {
            holder.timeTextView.setText("");
        }
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView, contentTextView, timeTextView;
        LinearLayout bubbleContainer, mainContainer;
        ImageView avatarImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            bubbleContainer = itemView.findViewById(R.id.bubbleContainer);
            mainContainer = itemView.findViewById(R.id.mainContainer);

        }
    }
}
