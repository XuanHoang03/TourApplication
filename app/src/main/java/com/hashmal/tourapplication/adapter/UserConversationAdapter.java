package com.hashmal.tourapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.activity.ChatActivity;
import com.hashmal.tourapplication.entity.UserConversation;

import java.util.List;

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
        UserConversation conversation = conversations.get(position);
        holder.lastMessage.setText("Last Message: " + conversation.getLastMessageContent());
        holder.timestamp.setText(conversation.getLastSend().toString());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("conversationId", conversation.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView lastMessage, timestamp;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            lastMessage = itemView.findViewById(R.id.lastMessageTextView);
            timestamp = itemView.findViewById(R.id.timestampTextView);
        }
    }

    public void updateList(List<UserConversation> newList) {
        this.conversations.clear();
        this.conversations.addAll(newList);
        notifyDataSetChanged();
    }
}
