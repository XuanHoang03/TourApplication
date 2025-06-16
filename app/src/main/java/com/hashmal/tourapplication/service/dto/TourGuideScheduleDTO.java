package com.hashmal.tourapplication.service.dto;

import java.time.LocalDateTime;

public class TourGuideScheduleDTO {
    private String tourScheduleId;
    private String tourId;
    private String startTime;
    private String endTime;
    private Integer number_of_ticket;
    private String tourGuideId;
    private String status;
    private String duration;
    private String tourName;

    public TourGuideScheduleDTO(String tourScheduleId, String tourId, String startTime, String endTime, Integer number_of_ticket, String tourGuideId, String status, String duration, String tourName) {
        this.tourScheduleId = tourScheduleId;
        this.tourId = tourId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.number_of_ticket = number_of_ticket;
        this.tourGuideId = tourGuideId;
        this.status = status;
        this.duration = duration;
        this.tourName = tourName;
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

    public Integer getNumber_of_ticket() {
        return number_of_ticket;
    }

    public void setNumber_of_ticket(Integer number_of_ticket) {
        this.number_of_ticket = number_of_ticket;
    }

    public String getTourGuideId() {
        return tourGuideId;
    }

    public void setTourGuideId(String tourGuideId) {
        this.tourGuideId = tourGuideId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }
}
