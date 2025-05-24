package com.hashmal.tourapplication.service.dto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Parse and hold the whole JSON blob returned by the Tour API.
 *
 *  Usage:
 *  TourDetailResponse dto = TourDetailResponse.fromJson(jsonString);
 */
public class YourTourDTO {

    @SerializedName("tourLocationList")
    private List<TourLocation> tourLocationList;

    @SerializedName("tour")
    private Tour tour;

    @SerializedName("tourPackage")
    private TourPackage tourPackage;

    @SerializedName("tourSchedule")
    private TourSchedule tourSchedule;

    @SerializedName("booking")
    private Booking booking;



    public List<TourLocation> getTourLocationList() { return tourLocationList; }
    public Tour getTour()                         { return tour; }
    public TourPackage getTourPackage()           { return tourPackage; }
    public TourSchedule getTourSchedule()         { return tourSchedule; }
    public Booking getBooking()                   { return booking; }

    /* ====================== INNER DTOs ====================== */

    public static class TourLocation {
        @SerializedName("location")
        private Location location;
        @SerializedName("visitOrder")
        private int visitOrder;

        public Location getLocation() { return location; }
        public int getVisitOrder()    { return visitOrder; }
    }

    public static class Location {
        private long id;
        private double latitude;
        private double longitude;
        private String country;
        private String province;
        private String city;
        private String fullAddress;
        private int openingHour;
        private int closingHour;
        private String description;
        private String name;
        private String thumbnailUrl;

        /* Getters omitted for brevity – generate if needed */
        public long   getId()          { return id; }
        public double getLatitude()    { return latitude; }
        public double getLongitude()   { return longitude; }
        public String getCountry()     { return country; }
        public String getProvince()    { return province; }
        public String getCity()        { return city; }
        public String getFullAddress() { return fullAddress; }
        public int    getOpeningHour() { return openingHour; }
        public int    getClosingHour() { return closingHour; }
        public String getDescription() { return description; }
        public String getName()        { return name; }
        public String getThumbnailUrl(){ return thumbnailUrl; }
    }

    public static class Tour {
        @SerializedName("tourId")
        private String tourId;
        @SerializedName("tourName")
        private String tourName;
        @SerializedName("tourType")
        private String tourType;
        private int status;
        @SerializedName("tourDescription")
        private String tourDescription;
        private int numberOfPeople;
        private boolean haveTourGuide;
        private String duration;
        private String thumbnailUrl;
        private String currentStartTime;
        private String currentEndTime;

        // getters
        public String getTourId() { return tourId; }
        public String getTourName() { return tourName; }
        public String getTourType() { return tourType; }
        public int getStatus() { return status; }
        public String getTourDescription() { return tourDescription; }
        public int getNumberOfPeople() { return numberOfPeople; }
        public boolean isHaveTourGuide() { return haveTourGuide; }
        public String getDuration() { return duration; }
        public String getThumbnailUrl() { return thumbnailUrl; }
        public String getCurrentStartTime() { return currentStartTime; }
        public String getCurrentEndTime() { return currentEndTime; }
    }

    public static class TourPackage {
        private long id;
        private String tourId;
        private String packageName;
        private String description;
        private long price;
        private int status;
        private boolean main;

        // getters
        public long getId() { return id; }
        public String getTourId() { return tourId; }
        public String getPackageName() { return packageName; }
        public String getDescription() { return description; }
        public long getPrice() { return price; }
        public int getStatus() { return status; }
        public boolean isMain() { return main; }
    }

    public static class TourSchedule {
        private String tourScheduleId;
        private String tourId;
        private String startTime; // ISO 8601 string
        private String endTime;
        @SerializedName("number_of_ticket")
        private int numberOfTicket;
        private String tourGuideId;
        private Integer status;

        // getters
        public String getTourScheduleId() { return tourScheduleId; }
        public String getTourId() { return tourId; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public int getNumberOfTicket() { return numberOfTicket; }
        public String getTourGuideId() { return tourGuideId; }
        public Integer getStatus() { return status; }
    }

    public static class Booking {
        private long id;
        private String userId;
        private String tourScheduleId;
        private long tourPackageId;
        private int quantity;
        private long totalPrice;
        private String bookingTime; // ISO 8601 string with microseconds
        private int paymentStatus;
        private String paymentMethod;

        // getters
        public long getId() { return id; }
        public String getUserId() { return userId; }
        public String getTourScheduleId() { return tourScheduleId; }
        public long getTourPackageId() { return tourPackageId; }
        public int getQuantity() { return quantity; }
        public long getTotalPrice() { return totalPrice; }
        public String getBookingTime() { return bookingTime; }
        public int getPaymentStatus() { return paymentStatus; }
        public String getPaymentMethod() { return paymentMethod; }
    }
}
