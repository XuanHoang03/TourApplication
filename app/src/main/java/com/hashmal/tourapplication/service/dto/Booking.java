package com.hashmal.tourapplication.service.dto;

import java.time.LocalDateTime;

public class Booking {
    private Long id;
    private String userId;
    private String tourScheduleId;
    private Long tourPackageId;
    private Integer quantity;
    private Long totalPrice;
    private String bookingTime;
    private Integer paymentStatus;
    private String paymentMethod;

    public Booking() {
    }

    public Booking(Long id, String userId, String tourScheduleId, Long tourPackageId, Integer quantity, Long totalPrice, String bookingTime, Integer paymentStatus, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.tourScheduleId = tourScheduleId;
        this.tourPackageId = tourPackageId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.bookingTime = bookingTime;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}