package com.hashmal.tourapplication.service.dto;

import java.util.List;

public class UpdateTourRequest {
    private String tourName;
    private String tourType;
    private String tourDescription;
    private Integer numberOfPeople;
    private String duration;
    private String thumbnailUrl;
    private String currentStartTime;
    private String currentEndTime;
    private List<CreateTourRequest.VisitOrderDTO> listLocationOrder;

    public UpdateTourRequest(String tourName, String tourType, String tourDescription, Integer numberOfPeople, String duration, String thumbnailUrl, String currentStartTime, String currentEndTime, List<CreateTourRequest.VisitOrderDTO> listLocationOrder) {
        this.tourName = tourName;
        this.tourType = tourType;
        this.tourDescription = tourDescription;
        this.numberOfPeople = numberOfPeople;
        this.duration = duration;
        this.thumbnailUrl = thumbnailUrl;
        this.currentStartTime = currentStartTime;
        this.currentEndTime = currentEndTime;
        this.listLocationOrder = listLocationOrder;
    }

    public UpdateTourRequest() { }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getTourType() {
        return tourType;
    }

    public void setTourType(String tourType) {
        this.tourType = tourType;
    }

    public String getTourDescription() {
        return tourDescription;
    }

    public void setTourDescription(String tourDescription) {
        this.tourDescription = tourDescription;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCurrentStartTime() {
        return currentStartTime;
    }

    public void setCurrentStartTime(String currentStartTime) {
        this.currentStartTime = currentStartTime;
    }

    public String getCurrentEndTime() {
        return currentEndTime;
    }

    public void setCurrentEndTime(String currentEndTime) {
        this.currentEndTime = currentEndTime;
    }

    public List<CreateTourRequest.VisitOrderDTO> getListLocationOrder() {
        return listLocationOrder;
    }

    public void setListLocationOrder(List<CreateTourRequest.VisitOrderDTO> listLocationOrder) {
        this.listLocationOrder = listLocationOrder;
    }
}
