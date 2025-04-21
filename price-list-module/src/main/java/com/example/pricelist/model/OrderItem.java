package com.example.pricelist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing an item in an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private String productCategory;
    private String productBrand;
    private double sellingPrice;
    private int quantity;
    
    /**
     * Calculate the subtotal for this order item
     * 
     * @return The subtotal (price * quantity)
     */
    public double getSubtotal() {
        return sellingPrice * quantity;
    }
    
    /**
     * Create an OrderItem from a CartItem
     * 
     * @param cartItem The cart item
     * @param orderId The order ID
     * @return A new OrderItem
     */
    public static OrderItem fromCartItem(CartItem cartItem, String orderId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setProductId(cartItem.getProductId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setSellingPrice(cartItem.getSellingPrice());
        
        // Copy product details to ensure they don't change if product is updated later
        Product product = cartItem.getProduct();
        if (product != null) {
            orderItem.setProductName(product.getName());
            orderItem.setProductCategory(product.getCategory());
            orderItem.setProductBrand(product.getBrand());
        }
        
        return orderItem;
    }
}
