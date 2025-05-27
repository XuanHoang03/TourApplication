package com.hashmal.tourapplication.service.dto;

public class SysUserDTO {
    private SystemAccount account;
    private Profile profile;

    public SysUserDTO(SystemAccount account, Profile profile) {
        this.account = account;
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public SystemAccount getAccount() {
        return account;
    }

    public void setAccount(SystemAccount account) {
        this.account = account;
    }
}
