package com.hashmal.tourapplication.service.dto;

import java.time.LocalDateTime;

public class SystemAccount {
    private String accountId;
    private String username;
    private String password;
    private String createdBy;
    private String createdAt;
    private String roleName;
    private Integer status;
    private String profileId;

    public SystemAccount() {
    }

    public SystemAccount(String accountId, String username, String password, String createdBy, String createdAt, String roleName, Integer status, String profileId) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.roleName = roleName;
        this.status = status;
        this.profileId = profileId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }
}
