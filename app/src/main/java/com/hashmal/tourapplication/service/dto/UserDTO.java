package com.hashmal.tourapplication.service.dto;

public class UserDTO {
    private Account account;
    private Profile profile;

    public UserDTO(Account account, Profile profile) {
        this.account = account;
        this.profile = profile;
    }

    public UserDTO() {
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
