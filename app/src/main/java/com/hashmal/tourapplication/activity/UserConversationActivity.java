package com.hashmal.tourapplication.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hashmal.tourapplication.R;
import com.hashmal.tourapplication.adapter.UserConversationAdapter;
import com.hashmal.tourapplication.entity.UserConversation;
import com.hashmal.tourapplication.service.FirebaseService;
import com.hashmal.tourapplication.service.LocalDataService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserConversationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserConversationAdapter adapter;
    private List<UserConversation> conversationList = new ArrayList<>();
    private FirebaseService firebaseService;
    private Boolean isFirstTime = true;
    private LocalDataService localDataService = LocalDataService.getInstance(this);
    private TextView tvNotify;
    private ImageView btnBack;
    private ProgressBar progressBar;
    String currentUserId = "{userId}";
    private static final String TAG = "USER_CONVERSATION_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_conversation_activity);
//        currentUserId = localDataService.getCurrentUser().getAccount().getAccountId();
        progressBar = findViewById(R.id.progressLoading);
        progressBar.setVisibility(VISIBLE);
        tvNotify = findViewById(R.id.tvNotify);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        firebaseService = new FirebaseService(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.recyclerViewConversations);
        adapter = new UserConversationAdapter(this, conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        firebaseService.listenForConversations(currentUserId, (snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to conversations", e);
                progressBar.setVisibility(GONE);
                tvNotify.setVisibility(VISIBLE);
                return;
            }

            if (snapshots != null && !snapshots.isEmpty()) {
                List<UserConversation> updated = new ArrayList<>();
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        String id = doc.getId();
                        String lastSendBy = doc.getString("lastSendBy");
                        String lastMessage = doc.getString("lastMessageContent");
                        Date timeSend = doc.getDate("lastSend");
                        Date updatedAt = doc.getDate("updatedAt");
                        String type = doc.getString("type");
                        updated.add(new UserConversation(id, lastSendBy, lastMessage, timeSend, updatedAt, type));
                    }
                runOnUiThread(() -> {

                    adapter.updateList(updated);
                    if (isFirstTime) {
                        isFirstTime = false;
                        progressBar.setVisibility(GONE);
                    }
                });
                });
            } else {
                tvNotify.setVisibility(VISIBLE);
            }
        });
    }


}
