package com.mobipos.app.Cashier.DO;

public class ProductData {
    private String id;
    private String name;
    private String tax;
    private String lowStockCount;
    private String measurementName;
    private String price;
    private String priceId;
    private String stock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(String lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceId() {
        return priceId;
    }

    public void setPriceId(String priceId) {
        this.priceId = priceId;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
