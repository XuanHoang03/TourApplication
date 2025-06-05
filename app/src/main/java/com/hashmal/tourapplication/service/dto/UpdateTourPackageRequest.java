package com.hashmal.tourapplication.service.dto;

public class UpdateTourPackageRequest {
    private Long id;
    private String tourId;
    private String packageName;
    private String description;
    private Long price;
    private Integer status;

    public UpdateTourPackageRequest() {}

    public UpdateTourPackageRequest(Long id, String packageName, String description, Long price, Integer status) {
        this.id = id;
        this.packageName = packageName;
        this.description = description;
        this.price = price;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
} 