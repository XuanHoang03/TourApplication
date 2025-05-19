package com.hashmal.tourapplication.service.dto;

import java.time.LocalDateTime;

public class TourScheduleResponseDTO {
    private String tourScheduleId;
    private String tourId;
    private String startTime;
    private String endTime;
    private Integer numberOfTicket;
    private String tourGuideId;

    public TourScheduleResponseDTO(String tourScheduleId, String tourId, String startTime, String endTime, Integer numberOfTicket, String tourGuideId) {
        this.tourScheduleId = tourScheduleId;
        this.tourId = tourId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfTicket = numberOfTicket;
        this.tourGuideId = tourGuideId;
    }

    public TourScheduleResponseDTO() {
    }

    public String getTourScheduleId() {
        return tourScheduleId;
    }

    public void setTourScheduleId(String tourScheduleId) {
        this.tourScheduleId = tourScheduleId;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getNumberOfTicket() {
        return numberOfTicket;
    }

    public void setNumberOfTicket(Integer numberOfTicket) {
        this.numberOfTicket = numberOfTicket;
    }

    public String getTourGuideId() {
        return tourGuideId;
    }

    public void setTourGuideId(String tourGuideId) {
        this.tourGuideId = tourGuideId;
    }
}
