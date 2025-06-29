package com.hashmal.tourapplication.entity;

import java.util.Date;

public class Message {
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    private String type;
    private String content;
    private String avatarUrl;

    public Message() {
        this.createdBy = "";
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.type = "";
        this.content = "";
        this.avatarUrl ="";
    }

    public Message(String createdBy, Date createdAt, Date updatedAt, String type, String content, String avatarUrl) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.content = content;
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
