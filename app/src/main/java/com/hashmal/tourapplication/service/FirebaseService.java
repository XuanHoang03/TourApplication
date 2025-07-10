package com.hashmal.tourapplication.service;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hashmal.tourapplication.callback.OnConversationReadyListener;
import com.hashmal.tourapplication.constants.FirebaseConst;
import com.hashmal.tourapplication.entity.UserConversation;
import com.hashmal.tourapplication.enums.MessageType;
import com.hashmal.tourapplication.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseService {
    private static final String TAG = "FIREBASE_SERVICE";
    private final FirebaseFirestore database;


    public FirebaseService(FirebaseFirestore database) {
        this.database = database;
    }

    public void sendMessage(String conversationId, String senderId, String content) {
        Date currentTime = new Date();
        Map<String, Object> message = DataUtils.buildFireStoreMessage(conversationId, senderId, content, currentTime, currentTime, MessageType.Normal.name());
        String messageId = conversationId.concat("_" + currentTime.getTime());

        database.collection(FirebaseConst.Conversation)
                .document(conversationId)
                .collection(FirebaseConst.MessagesSubCollection)
                .document(messageId)
                .set(message)
                .addOnSuccessListener(success -> {
                            Log.d(TAG, "Message sent with ID: " + messageId + message);
                            Map<String, Object> update = DataUtils.buildFireStoreUserConversation(currentTime, messageId, currentTime, content, MessageType.Normal.name(), senderId);

                            database.collection(FirebaseConst.Conversation)
                                    .document(conversationId)
                                    .get().addOnSuccessListener(doc -> {
                                        if (doc.exists()) {
                                            List<String> userIds = (List<String>) doc.get("listUserId");

                                            if (Objects.nonNull(userIds) && !userIds.isEmpty()) {
                                                for (String userId : userIds) {
                                                    database.collection(FirebaseConst.UserConversations)
                                                            .document(userId)
                                                            .collection(FirebaseConst.Conversation)
                                                            .document(conversationId)
                                                            .set(update)
                                                            .addOnSuccessListener(ifSuccess -> Log.d(TAG, "Conversation updated for user: " + userId))
                                                            .addOnFailureListener(ifFailure -> Log.d(TAG, "Conversation updated fail for user: " + userId, ifFailure));

                                                }
                                            } else {
                                                Log.w(TAG, "Conversation document not found: " + conversationId);
                                            }
                                        }
                                    }).addOnFailureListener(e ->
                                            Log.w(TAG, "Failed to fetch conversation for updating", e));
                        }
                )
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error sending message", e));
    }

    public void listenForConversations(String userId, EventListener<QuerySnapshot> listener) {
        database.collection(FirebaseConst.UserConversations)
                .document(userId)
                .collection(FirebaseConst.Conversation)
                .orderBy(FirebaseConst.UserConversationFields.lastSend, Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    public Task<List<String>> getListUserId(String conversationId) {
         return database.collection(FirebaseConst.Conversation).document(conversationId).get().continueWith(task -> {
             if (task.isSuccessful()) {
                 DocumentSnapshot doc = task.getResult();
                 if (doc.exists()) {
                     return (List<String>) doc.get("listUserId");
                 } else {
                     return new ArrayList<>();
                 }
             } else throw task.getException();
         });
    }
    public void getListUserInChat(String conversationId, OnUserIdsFetched callback) {
        FirebaseFirestore.getInstance()
                .collection("conversations")
                .document(conversationId)
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> userIds = (List<String>) doc.get("listUserId");
                    callback.onFetched(userIds);
                })
                .addOnFailureListener(e -> {
                    callback.onFetched(Collections.emptyList());
                });
    }

    public interface OnUserIdsFetched {
        void onFetched(List<String> userIds);
    }

    public void getOrCreateConversation(List<String> listUserId, OnConversationReadyListener listener) {

        List<String> sortedUserIds = new ArrayList<>(listUserId);
        Collections.sort(sortedUserIds);

        // Tạo Conversation ID duy nhất dựa trên userIds
        String conversationId = TextUtils.join("_", sortedUserIds);
        database.collection("conversations")
                .whereArrayContains("listUserId", listUserId.get(0)) // bắt đầu với user đầu tiên
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        List<String> existingList = (List<String>) doc.get("listUserId");
                        if (existingList != null && existingList.size() == listUserId.size()
                                && existingList.containsAll(listUserId)) {
                            // Cuộc trò chuyện đã tồn tại
                            listener.onReady(doc.getId());
                            return;
                        } else {
                            Map<String, Object> conversationData = new HashMap<>();
                            conversationData.put("listUserId", listUserId);
                            conversationData.put("createdAt", new Date());
                            conversationData.put("updatedAt", new Date());
                            database.collection("conversations")
                                    .document(conversationId)
                                    .set(conversationData)
                                    .addOnSuccessListener(unused -> {
                                        Log.d(TAG, "Conversation created");

                                        // Cập nhật cho từng user trong user_conversations
                                        Map<String, Object> userConv = new HashMap<>();
                                        userConv.put("lastMessageContent", null);
                                        userConv.put("lastSend", null);
                                        userConv.put("senderId", null);
                                        userConv.put("lastSendBy", null);
                                        userConv.put("type", "NORMAL");
                                        for (String userId : listUserId) {
                                            database.collection("user_conversations")
                                                    .document(userId)
                                                    .collection("conversations")
                                                    .document(conversationId)
                                                    .set(userConv);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.w("CHAT", "Failed to create conversation", e));
                        }
                    }
                })
                .addOnFailureListener(listener::onError);


    }

}
