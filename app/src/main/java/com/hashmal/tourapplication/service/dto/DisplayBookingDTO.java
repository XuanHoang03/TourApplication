package com.hashmal.tourapplication.service.dto;

import java.util.Date;

public class DisplayBookingDTO {
    private Long bookingId;
    private String tourScheduleId;
    private Long tourPackageId;
    private String bookingName;
    private Integer quantity;
    private Long totalPrice;
    private Integer paymentStatus;
    private String bookingTime;
    private String startTime;
    private String endTime;

    public DisplayBookingDTO() {
    }

    public DisplayBookingDTO(Long bookingId, String tourScheduleId, Long tourPackageId, String bookingName, Integer quantity, Long totalPrice, Integer paymentStatus, String bookingDate) {
        this.bookingId = bookingId;
        this.tourScheduleId = tourScheduleId;
        this.tourPackageId = tourPackageId;
        this.bookingName = bookingName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.bookingTime = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
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

    public String getBookingDate() {
        return bookingTime;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingTime = bookingDate;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getTourScheduleId() {
        return tourScheduleId;
    }

    public void setTourScheduleId(String tourScheduleId) {
        this.tourScheduleId = tourScheduleId;
    }

    public Long getTourPackageId() {
        return tourPackageId;
    }

    public void setTourPackageId(Long tourPackageId) {
        this.tourPackageId = tourPackageId;
    }

    public String getBookingName() {
        return bookingName;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
