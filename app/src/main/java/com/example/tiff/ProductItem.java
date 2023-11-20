package com.example.tiff;

public class ProductItem {
    private String productName;
    private String price;
    private String description;
    private String imageUrl;

    public ProductItem(String productName, String price, String description, String imageUrl) {
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getProductName() {return productName;}

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
