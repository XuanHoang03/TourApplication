package com.hashmal.tourapplication.service.dto;

public class CreateBookingRequest {
    private String userId;
    private String tourScheduleId;
    private Long tourPackageId;
    private Integer quantity;
    private String paymentMethod;

    public CreateBookingRequest(String userId, String tourScheduleId, Long tourPackageId, Integer quantity, String paymentMethod) {
        this.userId = userId;
        this.tourScheduleId = tourScheduleId;
        this.tourPackageId = tourPackageId;
        this.quantity = quantity;
        this.paymentMethod = paymentMethod;
    }

    public CreateBookingRequest() {

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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
