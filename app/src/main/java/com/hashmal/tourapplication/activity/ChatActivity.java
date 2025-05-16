package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.MessageAdapter;
import com.hashmal.tourapplication.constants.FirebaseConst;
import com.hashmal.tourapplication.entity.Message;
import com.hashmal.tourapplication.service.FirebaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "CHAT_ACTIVITY";

    private EditText messageEditText;
    private Button sendButton;
    private RecyclerView recyclerView;

    private MessageAdapter messageAdapter;
    private final List<Message> messageList = new ArrayList<>();

    private FirebaseService firebaseService;
    private String conversationId = "{conversationId}";  // Thay bằng conversationId thực tế

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);

        // RecyclerView setup
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        // Init FirebaseService
        firebaseService = new FirebaseService(FirebaseFirestore.getInstance());

        sendButton.setOnClickListener(v -> {
            String content = messageEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage("0", content); // "0" là ID giả định
            } else {
                Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
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

                            Message message = new Message(createdBy, createdAt, updatedAt, type, content);
                            messageList.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageList.size() - 1); // auto scroll
                    }
                });
    }
}
