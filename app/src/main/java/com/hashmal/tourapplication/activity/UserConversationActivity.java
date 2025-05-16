package com.hashmal.tourapplication.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.UserConversationAdapter;
import com.hashmal.tourapplication.entity.UserConversation;
import com.hashmal.tourapplication.service.FirebaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserConversationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserConversationAdapter adapter;
    private List<UserConversation> conversationList = new ArrayList<>();
    private FirebaseService firebaseService;

    String currentUserId = "{userId}";
    private static final String TAG = "USER_CONVERSATION_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_conversation_activity);

        firebaseService = new FirebaseService(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.recyclerViewConversations);
        adapter = new UserConversationAdapter(this, conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        firebaseService.listenForConversations(currentUserId, (snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to conversations", e);
                return;
            }

            if (snapshots != null && !snapshots.isEmpty()) {
                List<UserConversation> updated = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    String id = doc.getId();
                    String lastMessage = doc.getString("lastMessageContent");
                    Date timeSend = doc.getDate("lastSend");
                    Date updatedAt = doc.getDate("updatedAt");
                    String type = doc.getString("type");
                    updated.add(new UserConversation(id, lastMessage, timeSend, updatedAt, type));
                }

                adapter.updateList(updated);
            }
        });
    }



}
