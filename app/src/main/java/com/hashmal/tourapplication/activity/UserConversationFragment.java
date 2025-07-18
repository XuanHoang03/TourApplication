package com.hashmal.tourapplication.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class UserConversationFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserConversationAdapter adapter;
    private List<UserConversation> conversationList = new ArrayList<>();
    private FirebaseService firebaseService;
    private Boolean isFirstTime = true;
    private LocalDataService localDataService;
    private TextView tvNotify;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String currentUserId;
    private static final String TAG = "USER_CONVERSATION_FRAGMENT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init
        localDataService = LocalDataService.getInstance(requireContext());
        currentUserId = localDataService.getCurrentUser().getAccount().getAccountId();
        apiService = ApiClient.getApiService();
        firebaseService = new FirebaseService(FirebaseFirestore.getInstance());

        // View binding
        progressBar = view.findViewById(R.id.progressLoading);
        tvNotify = view.findViewById(R.id.tvNotify);
        recyclerView = view.findViewById(R.id.recyclerViewConversations);


        adapter = new UserConversationAdapter(requireContext(), conversationList, new HashMap<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v1 -> {
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
                    .thenAccept(mapResult -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Log.d(TAG, "Updating adapter with " + updated.size() + " conversations");
                                Log.d(TAG, "User chat info map size: " + mapResult.size());
                                adapter.updateList(updated, mapResult);
                                if (isFirstTime) {
                                    isFirstTime = false;
                                    progressBar.setVisibility(GONE);
                                }
                                Log.d(TAG, "Adapter item count after update: " + adapter.getItemCount());
                            });
                        }
                    })
                    .exceptionally(ex -> {
                        Log.e(TAG, "Error loading data", ex);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                progressBar.setVisibility(GONE);
                                tvNotify.setVisibility(VISIBLE);
                            });
                        }
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