package com.hashmal.tourapplication.service.dto;

public class CreateReviewRequest {
    private String userId;
    private String tourId;
    private String bookingId;
    private Integer rating;
    private String comment;

    public CreateReviewRequest() {
    }

    public CreateReviewRequest(String userId, String tourId, String bookingId, Integer rating, String comment) {
        this.tourId = tourId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
