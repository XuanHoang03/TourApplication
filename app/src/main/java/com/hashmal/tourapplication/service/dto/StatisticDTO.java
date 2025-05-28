package com.hashmal.tourapplication.service.dto;

public class StatisticDTO {
    private Long totalUsers;
    private Long totalTourPrograms;
    private Long totalBookings;
    private Long totalStaffs;

    public Long getTotalUsers() {
        return this.totalUsers;
    }

    public Long getTotalTourPrograms() {
        return this.totalTourPrograms;
    }

    public Long getTotalBookings() {
        return this.totalBookings;
    }

    public Long getTotalStaffs() {
        return this.totalStaffs;
    }

    public void setTotalUsers(final Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setTotalTourPrograms(final Long totalTourPrograms) {
        this.totalTourPrograms = totalTourPrograms;
    }


    public void setTotalBookings(final Long totalBookings) {
        this.totalBookings = totalBookings;
    }


    public void setTotalStaffs(final Long totalStaffs) {
        this.totalStaffs = totalStaffs;
    }


    public StatisticDTO() {
    }


    public StatisticDTO(final Long totalUsers, final Long totalTourPrograms, final Long totalBookings, final Long totalStaffs) {
        this.totalUsers = totalUsers;
        this.totalTourPrograms = totalTourPrograms;
        this.totalBookings = totalBookings;
        this.totalStaffs = totalStaffs;
    }
}