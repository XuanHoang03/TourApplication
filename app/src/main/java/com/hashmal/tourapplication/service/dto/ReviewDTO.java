package com.hashmal.tourapplication.service.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private String reviewerName;
    private Integer rating;
    private String comment;
    private String reviewTime;

    public ReviewDTO(String reviewerName, Integer rating, String comment, String reviewTime) {
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.reviewTime = reviewTime;
    }

    public ReviewDTO() {
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }
}
