package com.hashmal.tourapplication.service.dto;

import java.time.LocalDateTime;

public class Profile {
    private String profileId;
    private String fullName;
    private String email;
    private String province;

    private String country;
    private String city;
    private String district;
    private String address;
    private String phoneNumber;
    private String dob;
    private Integer gender;
    private String avatarUrl;
    private String frontIdCardUrl;
    private String backIdCardUrl;
    private Integer idType;
    private String idNumber;
    public Profile() {
    }

    public Profile(String profileId, String fullName, String email, String province, String country, String city, String district, String address, String phoneNumber, String dob, Integer gender, String avatarUrl, String frontIdCardUrl, String backIdCardUrl, Integer idType, String idNumber) {
        this.profileId = profileId;
        this.fullName = fullName;
        this.email = email;
        this.province = province;
        this.country = country;
        this.city = city;
        this.district = district;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.frontIdCardUrl = frontIdCardUrl;
        this.backIdCardUrl = backIdCardUrl;
        this.idType = idType;
        this.idNumber = idNumber;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFrontIdCardUrl() {
        return frontIdCardUrl;
    }

    public void setFrontIdCardUrl(String frontIdCardUrl) {
        this.frontIdCardUrl = frontIdCardUrl;
    }

    public String getBackIdCardUrl() {
        return backIdCardUrl;
    }

    public void setBackIdCardUrl(String backIdCardUrl) {
        this.backIdCardUrl = backIdCardUrl;
    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

}
