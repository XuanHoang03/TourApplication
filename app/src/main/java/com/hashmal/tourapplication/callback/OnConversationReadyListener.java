package com.hashmal.tourapplication.callback;

public interface OnConversationReadyListener {
    void onReady(String conversationId);
    void onError(Exception e);
}

