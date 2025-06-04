package com.hashmal.tourapplication.service.dto;

import java.util.List;

public class CreateTourRequest {
    // Thông tin chương trình
    private String tourName;
    private String tourType;
    private String tourDescription;
    private Integer numberOfPeople;
    private String duration;
    private String thumbnailUrl;

    // Thông tin địa điểm
    private List<VisitOrderDTO> listLocationOrder;

    private String currentStartTime;
    private String currentEndTime;

    public CreateTourRequest() {
    }

    public CreateTourRequest(String tourName, String tourType, String tourDescription, Integer numberOfPeople, String duration, String thumbnailUrl, List<VisitOrderDTO> listLocationOrder, String currentStartTime, String currentEndTime) {
        this.tourName = tourName;
        this.tourType = tourType;
        this.tourDescription = tourDescription;
        this.numberOfPeople = numberOfPeople;
        this.duration = duration;
        this.thumbnailUrl = thumbnailUrl;
        this.listLocationOrder = listLocationOrder;
        this.currentStartTime = currentStartTime;
        this.currentEndTime = currentEndTime;
    }

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

    public List<VisitOrderDTO> getListLocationOrder() {
        return listLocationOrder;
    }

    public void setListLocationOrder(List<VisitOrderDTO> listLocationOrder) {
        this.listLocationOrder = listLocationOrder;
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

    public static class VisitOrderDTO {
        private Long locationId;
        private Integer orderNumber;

        public VisitOrderDTO() {}
        public VisitOrderDTO(Long locationId, Integer orderNumber) {
            this.locationId = locationId;
            this.orderNumber = orderNumber;
        }
        public Long getLocationId() {
            return locationId;
        }
        public void setLocationId(Long locationId) {
            this.locationId = locationId;
        }
        public Integer getOrderNumber() {
            return orderNumber;
        }
        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }
    }
} 