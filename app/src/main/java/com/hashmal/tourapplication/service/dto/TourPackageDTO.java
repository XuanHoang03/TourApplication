package com.hashmal.tourapplication.service.dto;

public class TourPackageDTO {
    private Long id;
    private String packageName;

    public TourPackageDTO(Long id, String packageName, String description, Long price, boolean isMain) {
        this.id = id;
        this.packageName = packageName;
        this.description = description;
        this.price = price;
        this.isMain = isMain;
    }

    public TourPackageDTO() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    private String description;
    private Long price;
    private boolean isMain;
}
