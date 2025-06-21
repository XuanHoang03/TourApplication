package com.hashmal.tourapplication.service.dto;

public class UserBookingDTO {
    private DisplayBookingDTO booking;
    private UserDTO user;

    public UserBookingDTO() {
    }

    public UserBookingDTO(DisplayBookingDTO booking, UserDTO user) {
        this.booking = booking;
        this.user = user;
    }

    public DisplayBookingDTO getBooking() {
        return booking;
    }

    public void setBooking(DisplayBookingDTO booking) {
        this.booking = booking;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
} 