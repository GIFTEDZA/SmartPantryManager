package com.example.smartpantrymanager;

public class PantryItem {
    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // YYYY-MM-DD or empty
    private String category;   // e.g. "Vegetables & Fruit", "Dairy & Eggs", etc.

    public PantryItem() {
    }

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.category = category;
    }

    public PantryItem(String name, double quantity, String unit, String expiryDate, String category) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCategory() {
        return category != null ? category : "Pantry & Canned";
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
