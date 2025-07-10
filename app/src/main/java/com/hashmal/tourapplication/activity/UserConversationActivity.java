package com.hashmal.tourapplication.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.ArraySet;
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
import com.hashmal.tourapplication.network.ApiClient;
import com.hashmal.tourapplication.service.ApiService;
import com.hashmal.tourapplication.service.FirebaseService;
import com.hashmal.tourapplication.service.LocalDataService;
import com.hashmal.tourapplication.service.dto.UserChatInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConversationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserConversationAdapter adapter;
    private List<UserConversation> conversationList = new ArrayList<>();
    private FirebaseService firebaseService;
    private Boolean isFirstTime = true;
    private LocalDataService localDataService;
    private TextView tvNotify;
    private ImageView btnBack;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String currentUserId;
    private static final String TAG = "USER_CONVERSATION_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_conversation_activity);

        // Init
        localDataService = LocalDataService.getInstance(this);
        currentUserId = localDataService.getCurrentUser().getAccount().getAccountId();
        apiService = ApiClient.getApiService();
        firebaseService = new FirebaseService(FirebaseFirestore.getInstance());

        // View binding
        progressBar = findViewById(R.id.progressLoading);
        tvNotify = findViewById(R.id.tvNotify);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewConversations);

        btnBack.setOnClickListener(v -> finish());

        adapter = new UserConversationAdapter(this, conversationList, new HashMap<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(VISIBLE);

        firebaseService.listenForConversations(currentUserId, (snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to conversations", e);
                progressBar.setVisibility(GONE);
                tvNotify.setVisibility(VISIBLE);
                return;
            }

            if (snapshots == null || snapshots.isEmpty()) {
                progressBar.setVisibility(GONE);
                tvNotify.setVisibility(VISIBLE);
                return;
            }

            List<UserConversation> updated = new ArrayList<>();
            List<CompletableFuture<List<String>>> futures = new ArrayList<>();

            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                String id = doc.getId();
                String lastSendBy = doc.getString("lastSendBy");
                String lastMessage = doc.getString("lastMessageContent");
                Date timeSend = doc.getDate("lastSend");
                Date updatedAt = doc.getDate("updatedAt");
                String type = doc.getString("type");

                updated.add(new UserConversation(id, lastSendBy, lastMessage, timeSend, updatedAt, type));

                CompletableFuture<List<String>> future = new CompletableFuture<>();
                firebaseService.getListUserId(id)
                        .addOnSuccessListener(future::complete)
                        .addOnFailureListener(future::completeExceptionally);
                futures.add(future);
            }

            // Chờ toàn bộ getListUserId hoàn thành
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        Set<String> mergedIds = new ArraySet<>();
                        for (CompletableFuture<List<String>> f : futures) {
                            try {
                                mergedIds.addAll(f.get());
                            } catch (Exception ex) {
                                Log.e(TAG, "Error merging userIds", ex);
                            }
                        }
                        return new ArrayList<>(mergedIds);
                    })
                    .thenCompose(this::fetchUserChatInfoAsync)
                    .thenAccept(mapResult -> runOnUiThread(() -> {
                        adapter.updateList(updated, mapResult);
                        if (isFirstTime) {
                            isFirstTime = false;
                            progressBar.setVisibility(GONE);
                        }
                    }))
                    .exceptionally(ex -> {
                        Log.e(TAG, "Error loading data", ex);
                        runOnUiThread(() -> {
                            progressBar.setVisibility(GONE);
                            tvNotify.setVisibility(VISIBLE);
                        });
                        return null;
                    });
        });
    }

    public CompletableFuture<Map<String, UserChatInfo>> fetchUserChatInfoAsync(List<String> listId) {
        CompletableFuture<Map<String, UserChatInfo>> future = new CompletableFuture<>();

        apiService.getUserChatInfo(listId).enqueue(new Callback<Map<String, UserChatInfo>>() {
            @Override
            public void onResponse(Call<Map<String, UserChatInfo>> call, Response<Map<String, UserChatInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new RuntimeException("Response null or failed"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, UserChatInfo>> call, Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }
}
