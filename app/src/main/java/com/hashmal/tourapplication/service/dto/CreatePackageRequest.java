package com.hashmal.tourapplication.service.dto;

public class CreatePackageRequest {
    private String tourId;
    private String packageName;
    private String description;
    private Long price;
    private boolean isMain;

    public CreatePackageRequest(String tourId, String packageName, String description, Long price, boolean isMain) {
        this.tourId = tourId;
        this.packageName = packageName;
        this.description = description;
        this.price = price;
        this.isMain = isMain;
    }
    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
    public boolean isMain() { return isMain; }
    public void setMain(boolean main) { isMain = main; }
} 