package com.hashmal.tourapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.MessageAdapter;
import com.hashmal.tourapplication.constants.FirebaseConst;
import com.hashmal.tourapplication.entity.Message;
import com.hashmal.tourapplication.service.FirebaseService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.UserDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "CHAT_ACTIVITY";

    private EditText messageEditText;
    private ImageButton sendButton;
    private RecyclerView recyclerView;

    private MessageAdapter messageAdapter;
    private final List<Message> messageList = new ArrayList<>();
    private Gson gson = new Gson();
    private UserDTO otherUser;
    private UserDTO currentUser;
    private ImageButton btnBack;
    private ImageView avatarImageView;
    private TextView userNameTextView;
    private FirebaseService firebaseService;
    private LocalDataService localDataService = LocalDataService.getInstance(this);
    private String conversationId = "{conversationId}";  // Thay bằng conversationId thực tế

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        btnBack = findViewById(R.id.btnBack);
        avatarImageView = findViewById(R.id.avatarImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        btnBack.setOnClickListener(v-> finish());

        Intent intent = getIntent();
        currentUser = localDataService.getCurrentUser();
        conversationId = intent.getStringExtra("conversationId");
        String otherUserJson = intent.getStringExtra("otherUserJson");
        otherUser = gson.fromJson(otherUserJson, UserDTO.class);

        userNameTextView.setText(otherUser.getProfile().getFullName());
        if (otherUser.getProfile().getAvatarUrl() != null) {
            Glide.with(this)
                    .load(otherUser.getProfile().getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(avatarImageView);
        }

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);

        // RecyclerView setup
        messageAdapter = new MessageAdapter(messageList, currentUser.getAccount().getAccountId(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Init FirebaseService
        firebaseService = new FirebaseService(FirebaseFirestore.getInstance());

        sendButton.setOnClickListener(v -> {
            String content = messageEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(currentUser.getAccount().getAccountId(), content);
            } else {
                Toast.makeText(ChatActivity.this, "Nhập gì rồi hãy nhắn!", Toast.LENGTH_SHORT).show();
            }
        });

        // Real-time message listening
        getMessagesRealTime();
    }

    private void sendMessage(String senderId, String content) {
        firebaseService.sendMessage(conversationId, senderId, content);
        messageEditText.setText("");
    }

    private void getMessagesRealTime() {
        FirebaseFirestore.getInstance()
                .collection(FirebaseConst.Conversation)
                .document(conversationId)
                .collection(FirebaseConst.MessagesSubCollection)
                .orderBy("createdAt")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Error getting messages", e);
                        return;
                    }

                    if (snapshot != null) {
                        messageList.clear();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            String createdBy = document.getString("createdBy");
                            Date updatedAt = document.getDate("updatedAt");
                            String content = document.getString("content");
                            Date createdAt = document.getDate("createdAt");
                            String type = document.getString("type");

                            String name = (otherUser.getAccount().getAccountId().equals(createdBy)) ? otherUser.getProfile().getFullName() : "Bạn";
                            String avatarUrl = (otherUser.getAccount().getAccountId().equals(createdBy)) ? otherUser.getProfile().getAvatarUrl() : currentUser.getProfile().getAvatarUrl();

                            Message message = new Message(name, createdAt, updatedAt, type, content, avatarUrl);
                            messageList.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1); // auto scroll
                    }
                });
    }
}
