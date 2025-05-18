package com.hashmal.tourapplication.service.dto;

public class Account {
    private String accountId;
    private String username; // = phone number
    private String pin;

    public Account(String accountId, String username, String pin, Integer roleId, String roleName, Integer status, String profileId, String password) {
        this.accountId = accountId;
        this.username = username;
        this.pin = pin;
        this.roleId = roleId;
        this.roleName = roleName;
        this.status = status;
        this.profileId = profileId;
        this.password = password;
    }

    public Account() {
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Integer roleId;
    private String roleName;
    private Integer status;
    private String profileId;
    private String password;
}
