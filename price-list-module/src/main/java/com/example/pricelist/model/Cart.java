package com.example.pricelist.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Model class representing a shopping cart.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private String id;
    private List<CartItem> items = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    
    /**
     * Constructor with ID only (for new carts)
     */
    public Cart(String id) {
        this.id = id;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    /**
     * Add an item to the cart
     * 
     * @param product The product to add
     * @param quantity The quantity to add
     * @param sellingPrice The selling price
     * @return The added cart item
     */
    public CartItem addItem(Product product, int quantity, double sellingPrice) {
        // Check if the product is already in the cart
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            // Update existing item
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            this.updatedAt = new Date();
            return item;
        } else {
            // Add new item
            CartItem newItem = new CartItem(
                    product.getId(),
                    this.id,
                    quantity,
                    sellingPrice,
                    product
            );
            items.add(newItem);
            this.updatedAt = new Date();
            return newItem;
        }
    }
    
    /**
     * Update the quantity of an item in the cart
     * 
     * @param productId The product ID
     * @param quantity The new quantity
     * @return true if updated, false if not found
     */
    public boolean updateItemQuantity(String productId, int quantity) {
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(quantity);
            this.updatedAt = new Date();
            return true;
        }
        
        return false;
    }
    
    /**
     * Remove an item from the cart
     * 
     * @param productId The product ID to remove
     * @return true if removed, false if not found
     */
    public boolean removeItem(String productId) {
        boolean removed = items.removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            this.updatedAt = new Date();
        }
        return removed;
    }
    
    /**
     * Calculate the total price of all items in the cart
     * 
     * @return The total price
     */
    public double getTotal() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
    
    /**
     * Get the total number of items in the cart
     * 
     * @return The total number of items
     */
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    /**
     * Clear all items from the cart
     */
    public void clear() {
        items.clear();
        this.updatedAt = new Date();
    }
}
