package com.example.pricelist.model;

/**
 * Request object for adding items to the cart.
 * Used for binding form data with Thymeleaf.
 */
public class CartItemRequest {
    private String productId;
    private int quantity;
    private double sellingPrice;
    
    // Default constructor required for form binding
    public CartItemRequest() {
    }
    
    // Getters and setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getSellingPrice() {
        return sellingPrice;
    }
    
    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
