package com.hashmal.tourapplication.service.dto;

import java.util.List;

public class UserManagementDTO {
    private String totalUser;
    private String activeUser;
    private List<UserDTO> users;

    public UserManagementDTO() {
    }

    public UserManagementDTO(String totalUser, String activeUser, List<UserDTO> users) {
        this.totalUser = totalUser;
        this.activeUser = activeUser;
        this.users = users;
    }

    public String getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(String totalUser) {
        this.totalUser = totalUser;
    }

    public String getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(String activeUser) {
        this.activeUser = activeUser;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}
