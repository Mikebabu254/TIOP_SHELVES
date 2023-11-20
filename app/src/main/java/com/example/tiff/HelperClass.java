package com.example.tiff;

public class HelperClass {
    String productName;
    String price;
    String description;
    String imageUrl;  // Add this line

    public HelperClass(String productTxt, String priceTxt, String descriptionTxt, String imageUrl) {
        this.productName = productTxt;
        this.price = priceTxt;
        this.description = descriptionTxt;
        this.imageUrl = imageUrl;  // Add this line
    }

    public HelperClass(String productTxt, String priceTxt, String descriptionTxt) {
        this.productName = productTxt;
        this.price = priceTxt;
        this.description = descriptionTxt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HelperClass() {
    }
}

