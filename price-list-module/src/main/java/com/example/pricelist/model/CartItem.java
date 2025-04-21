package com.example.pricelist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing an item in a shopping cart.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String id;
    private String productId;
    private String cartId;
    private int quantity;
    private double sellingPrice; // Actual selling price (within min-max range)
    private Product product; // Reference to the product
    
    /**
     * Constructor without ID (for new cart items)
     */
    public CartItem(String productId, String cartId, int quantity, double sellingPrice, Product product) {
        this.productId = productId;
        this.cartId = cartId;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.product = product;
    }
    
    /**
     * Calculate the subtotal for this cart item
     * 
     * @return The subtotal (price * quantity)
     */
    public double getSubtotal() {
        return sellingPrice * quantity;
    }
}
