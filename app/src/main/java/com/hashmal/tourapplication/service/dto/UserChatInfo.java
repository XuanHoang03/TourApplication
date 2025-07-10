package com.hashmal.tourapplication.service.dto;

public class UserChatInfo {
    private String accountId;
    private String fullName;
    private String avatarUrl;


    public UserChatInfo(String accountId, String fullName, String avatarUrl) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
