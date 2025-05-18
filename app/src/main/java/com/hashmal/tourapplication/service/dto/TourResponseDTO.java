package com.hashmal.tourapplication.service.dto;

import java.util.List;

public class TourResponseDTO {
    private String tourId;
    private String tourName;
    private String tourType;
    private String tourDescription;
    private Long numberOfPeople;
    private Boolean haveTourGuide;
    private String duration;
    private String thumbnailUrl;

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
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

    public Long getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Long numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public Boolean getHaveTourGuide() {
        return haveTourGuide;
    }

    public void setHaveTourGuide(Boolean haveTourGuide) {
        this.haveTourGuide = haveTourGuide;
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

    public List<LocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }

    public List<TourPackageDTO> getPackages() {
        return packages;
    }

    public void setPackages(List<TourPackageDTO> packages) {
        this.packages = packages;
    }

    private String currentStartTime;
    private String currentEndTime;
    private List<LocationDTO> locations;
    private List<TourPackageDTO> packages;
}
