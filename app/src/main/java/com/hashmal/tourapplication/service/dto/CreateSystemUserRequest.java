package com.hashmal.tourapplication.service.dto;

import java.time.LocalDateTime;

public class CreateSystemUserRequest {
    private String fullName;
    private String phoneNumber;
    private String username;
    private String password;
    private String email;
    private String address;
    private String dob;
    private Integer gender;
    private String roleName;

    public CreateSystemUserRequest(String fullName, String phoneNumber, String username, String password, String email, String address, String dob, Integer gender, String roleName) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.roleName = roleName;
    }

    public CreateSystemUserRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
