package com.hashmal.tourapplication.service.dto;

import java.util.List;

public class StaffManagementDTO {
    private String totalStaff;
    private String activeStaff;
    private List<UserDTO> staffs;

    public StaffManagementDTO() {
    }

    public StaffManagementDTO(String totalStaff, String activeStaff, List<UserDTO> staffs) {
        this.totalStaff = totalStaff;
        this.activeStaff = activeStaff;
        this.staffs = staffs;
    }

    public String getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(String totalStaff) {
        this.totalStaff = totalStaff;
    }

    public String getActiveStaff() {
        return activeStaff;
    }

    public void setActiveStaff(String activeStaff) {
        this.activeStaff = activeStaff;
    }

    public List<UserDTO> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<UserDTO> staffs) {
        this.staffs = staffs;
    }
} 