package com.hashmal.tourapplication.entity;

import java.util.Date;

public class UserConversation {
    private String id;
    private String lastSendBy;
    private String lastMessageContent;
    private Date lastSend;
    private Date updatedAt;
    private String type;

    public UserConversation() {
        this.id = "";
        this.lastMessageContent = "";
        this.lastSend = new Date();
        this.updatedAt = new Date();
        this.type = "";
    }

    public UserConversation(String id, String lastSendBy, String lastMessageContent, Date lastMessageTime, Date updatedAt, String type) {
        this.id = id;
        this.lastMessageContent = lastMessageContent;
        this.lastSend = lastMessageTime;
        this.updatedAt = updatedAt;
        this.type = type;
        this.lastSendBy = lastSendBy;
    }

    public String getLastSendBy() {
        return lastSendBy;
    }

    public void setLastSendBy(String lastSendBy) {
        this.lastSendBy = lastSendBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public Date getLastSend() {
        return lastSend;
    }

    public void setLastSend(Date lastMessageTime) {
        this.lastSend = lastMessageTime;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
