package com.hashmal.tourapplication.constants;

import java.util.Date;

public class FirebaseConst {
    public static String Conversation = "conversations";
    public static String UserConversations = "user_conversations";
    public static String MessagesSubCollection = "messages";


    public static class MessageFields {
        public static String createdBy = "createdBy";
        public static String content = "content";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";
        public static String type = "type";
    }

    public static class UserConversationFields {
        public static String id = "id";
        public static String lastMessageContent = "lastMessageContent";
        public static String lastSend = "lastSend";
        public static String updatedAt = "updatedAt";
        public static String type = "type";
    }
}
